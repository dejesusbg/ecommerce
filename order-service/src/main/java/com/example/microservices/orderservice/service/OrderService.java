package com.example.microservices.orderservice.service;

import com.example.microservices.orderservice.client.InventoryServiceClient;
import com.example.microservices.orderservice.client.ProductServiceClient;
import com.example.microservices.orderservice.dto.*;
import com.example.microservices.orderservice.model.Order;
import com.example.microservices.orderservice.model.OrderItem;
import com.example.microservices.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    public OrderService(OrderRepository orderRepository,
            ProductServiceClient productServiceClient,
            InventoryServiceClient inventoryServiceClient) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        logger.info("Attempting to create order for customer: {}", orderRequestDTO.getCustomerId());

        Order order = new Order(orderRequestDTO.getCustomerId());
        List<OrderItem> orderItems = new ArrayList<>();
        double totalOrderPrice = 0.0;

        // 1. Validate products and inventory
        for (OrderItemRequestDTO itemRequest : orderRequestDTO.getItems()) {
            String productIdStr = itemRequest.getProductId();
            UUID productId;
            try {
                productId = UUID.fromString(productIdStr);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid product ID format: {}", productIdStr);
                order.setOrderStatus("FAILED_INVALID_PRODUCT_ID");
                throw new IllegalArgumentException("Invalid product ID format: " + productIdStr);
            }

            logger.info("Fetching product details for ID: {}", productIdStr);
            ResponseEntity<ProductDTO> productResponse = productServiceClient.getProductById(productIdStr);

            if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
                logger.error("Failed to fetch product details for ID: {}. Status: {}", productIdStr,
                        productResponse.getStatusCode());
                order.setOrderStatus("FAILED_PRODUCT_NOT_FOUND");
                throw new RuntimeException("Product not found: " + productIdStr);
            }
            ProductDTO productDTO = productResponse.getBody();
            logger.info("Product details fetched: {}", productDTO);

            logger.info("Fetching inventory for product ID: {}", productId);
            ResponseEntity<Integer> inventoryResponse = inventoryServiceClient.getInventoryByProductId(productId);

            if (!inventoryResponse.getStatusCode().is2xxSuccessful() || inventoryResponse.getBody() == null) {
                logger.error("Failed to fetch inventory for product ID: {}. Status: {}", productId,
                        inventoryResponse.getStatusCode());
                order.setOrderStatus("FAILED_INVENTORY_CHECK_FAILED");
                throw new RuntimeException("Inventory check failed for product: " + productId);
            }
            int currentStock = inventoryResponse.getBody();
            logger.info("Current stock for product ID {}: {}", productId, currentStock);

            if (currentStock < itemRequest.getQuantity()) {
                logger.warn("Insufficient stock for product ID {}. Requested: {}, Available: {}", productId,
                        itemRequest.getQuantity(), currentStock);
                order.setOrderStatus("FAILED_INSUFFICIENT_STOCK");
                throw new RuntimeException("Insufficient stock for product: " + productDTO.getName());
            }

            OrderItem orderItem = new OrderItem(productId, itemRequest.getQuantity(), productDTO.getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
            totalOrderPrice += (productDTO.getPrice() * itemRequest.getQuantity());
        }

        order.setOrderItems(orderItems);
        order.setOrderStatus("PROCESSING"); // Initial status before payment/further steps
        Order savedOrder = orderRepository.save(order);
        logger.info("Order {} saved with status PROCESSING.", savedOrder.getId());

        // 2. Update inventory (post-order persistence)
        try {
            for (OrderItem item : savedOrder.getOrderItems()) {
                ResponseEntity<Integer> currentInventoryResponse = inventoryServiceClient
                        .getInventoryByProductId(item.getProductId());
                int currentQuantity = 0;
                if (currentInventoryResponse.getStatusCode().is2xxSuccessful()
                        && currentInventoryResponse.getBody() != null) {
                    currentQuantity = currentInventoryResponse.getBody();
                } else {
                    // This scenario means we could not re-fetch inventory, which is problematic.
                    logger.error(
                            "Critical: Could not re-fetch inventory for product {} before update. Assuming previous check was valid but proceeding with caution.",
                            item.getProductId());
                    // Or, more conservatively:
                    // throw new RuntimeException("Failed to re-confirm inventory for product " +
                    // item.getProductId() + " before update.");
                }

                int newQuantity = currentQuantity - item.getQuantity();
                InventoryUpdateRequestDTO updateRequest = new InventoryUpdateRequestDTO(item.getProductId(),
                        newQuantity);

                logger.info("Updating inventory for product ID {}. New quantity: {}", item.getProductId(), newQuantity);
                ResponseEntity<InventoryDTO> updateResponse = inventoryServiceClient.updateInventory(updateRequest);

                if (!updateResponse.getStatusCode().is2xxSuccessful()) {
                    logger.error("Failed to update inventory for product ID {}. Status: {}", item.getProductId(),
                            updateResponse.getStatusCode());
                    savedOrder.setOrderStatus("FAILED_INVENTORY_UPDATE");
                    orderRepository.save(savedOrder); // Update status
                    throw new RuntimeException("Inventory update failed for product: " + item.getProductId());
                }
                logger.info("Inventory updated successfully for product ID {}", item.getProductId());
            }
        } catch (Exception e) {
            logger.error("Error during inventory update phase for order {}: {}", savedOrder.getId(), e.getMessage());
            if (!"FAILED_INVENTORY_UPDATE".equals(savedOrder.getOrderStatus())) {
                savedOrder.setOrderStatus("FAILED_INVENTORY_UPDATE_ERROR");
                orderRepository.save(savedOrder);
            }
            throw e;
            // Re-throw to ensure transaction rollback if not committed + to inform caller.
        }

        savedOrder.setOrderStatus("COMPLETED"); // Or PENDING_PAYMENT if payment is the next step
        Order finalOrder = orderRepository.save(savedOrder);
        logger.info("Order {} successfully processed and status set to {}.", finalOrder.getId(),
                finalOrder.getOrderStatus());

        return convertToOrderResponseDTO(finalOrder, totalOrderPrice);
    }

    public OrderResponseDTO getOrderById(UUID id) {
        logger.info("Fetching order by ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Order not found for ID: {}", id);
                    return new RuntimeException("Order not found: " + id);
                });

        // Calculate total price for the response
        double totalPrice = order.getOrderItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        logger.info("Order found: {}. Total price calculated: {}", order.getId(), totalPrice);
        return convertToOrderResponseDTO(order, totalPrice);
    }

    private OrderResponseDTO convertToOrderResponseDTO(Order order, Double calculatedTotalPrice) {
        List<OrderItemResponseDTO> itemResponseDTOs = order.getOrderItems().stream()
                .map(oi -> {
                    String productName = "N/A"; // Placeholder
                    try {
                        ResponseEntity<ProductDTO> productResponse = productServiceClient
                                .getProductById(oi.getProductId().toString());
                        if (productResponse.getStatusCode().is2xxSuccessful() && productResponse.getBody() != null) {
                            productName = productResponse.getBody().getName();
                        }
                    } catch (Exception e) {
                        logger.warn("Could not fetch product name for product ID {} during DTO conversion: {}",
                                oi.getProductId(), e.getMessage());
                    }
                    return new OrderItemResponseDTO(oi.getId(), oi.getProductId(), oi.getQuantity(), oi.getPrice(),
                            productName);
                })
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomerId(),
                order.getOrderDate(),
                order.getOrderStatus(),
                itemResponseDTOs,
                calculatedTotalPrice);
    }
}
