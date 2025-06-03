'use client';

import Image from "next/image";
import { useState, useEffect } from 'react';
import axios from 'axios';
import { useTranslation } from 'react-i18next';

export default function Home() {
  const { t } = useTranslation('common');
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [tokenLoading, setTokenLoading] = useState<boolean>(true);
  const [tokenError, setTokenError] = useState<string | null>(null);

  // API call states
  const initialApiState = { data: null, loading: false, error: null };
  const [productsResult, setProductsResult] = useState(initialApiState);
  const [createProductResult, setCreateProductResult] = useState(initialApiState);
  const [updateInventoryResult, setUpdateInventoryResult] = useState(initialApiState);
  const [createOrderResult, setCreateOrderResult] = useState(initialApiState);
  const [processPaymentResult, setProcessPaymentResult] = useState(initialApiState);

  const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

  useEffect(() => {
    const fetchToken = async () => {
      try {
        setTokenLoading(true);
        setTokenError(null);

        const params = new URLSearchParams();
        params.append('client_id', 'api-gateway-client');
        params.append('username', 'testuser');
        params.append('password', 'password');
        params.append('grant_type', 'password');
        params.append('client_secret', process.env.NEXT_PUBLIC_KEYCLOAK_CLIENT_SECRET || '');

        const response = await axios.post(
          'http://localhost:8180/realms/microservices-realm/protocol/openid-connect/token',
          params,
          {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
            },
          }
        );

        setAccessToken(response.data.access_token);
        console.log('Access Token:', response.data.access_token);
      } catch (err: any) {
        setTokenError(err.message || 'Failed to fetch token');
        console.error('Token fetch error:', err);
      } finally {
        setTokenLoading(false);
        console.log('Token loading state:', false);
      }
    };

    fetchToken();
  }, []);

  useEffect(() => {
    if (!accessToken) return;

    const performApiSequence = async () => {
      let createdProductId: string | null = null;
      let createdOrderId: string | null = null;
      const productPrice = 1200.00;

      // 1. Get All Products
      try {
        setProductsResult({ data: null, loading: true, error: null });
        console.log('API: Getting all products...');
        const response = await axios.get('http://localhost:8080/api/products', {
          headers: { 'Authorization': `Bearer ${accessToken}` },
        });
        setProductsResult({ data: response.data, loading: false, error: null });
        console.log('API: Get All Products Success:', response.data);
      } catch (err: any) {
        setProductsResult({ data: null, loading: false, error: err.message || 'Failed' });
        console.error('API: Get All Products Error:', err);
        return; // Stop sequence if this fails
      }

      await delay(1000);

      // 2. Create Product
      try {
        setCreateProductResult({ data: null, loading: true, error: null });
        console.log('API: Creating product...');
        const productData = { name: "New Laptop", description: "A powerful new laptop", price: productPrice };
        const response = await axios.post('http://localhost:8080/api/products', productData, {
          headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
        });
        createdProductId = response.data.id; // Assuming the response has an 'id' field
        setCreateProductResult({ data: response.data, loading: false, error: null });
        console.log('API: Create Product Success:', response.data);
        if (!createdProductId) {
            console.error('API: Create Product Error: Product ID not found in response.');
            setCreateProductResult({ data: response.data, loading: false, error: 'Product ID not found' });
            return;
        }
      } catch (err: any) {
        setCreateProductResult({ data: null, loading: false, error: err.message || 'Failed' });
        console.error('API: Create Product Error:', err);
        return;
      }

      await delay(1000);

      // 3. Update Inventory
      if (createdProductId) {
        try {
          setUpdateInventoryResult({ data: null, loading: true, error: null });
          console.log(`API: Updating inventory for product ${createdProductId}...`);
          const inventoryData = { productId: createdProductId, newQuantity: 10 };
          const response = await axios.post('http://localhost:8080/api/inventory', inventoryData, {
            headers: {
              'Authorization': `Bearer ${accessToken}`,
              'Content-Type': 'application/json',
            },
          });
          setUpdateInventoryResult({ data: response.data, loading: false, error: null });
          console.log('API: Update Inventory Success:', response.data);
        } catch (err: any) {
          setUpdateInventoryResult({ data: null, loading: false, error: err.message || 'Failed' });
          console.error('API: Update Inventory Error:', err);
          return;
        }
      }

      await delay(1000);

      // 4. Create Order
      if (createdProductId) {
        try {
          setCreateOrderResult({ data: null, loading: true, error: null });
          console.log(`API: Creating order for product ${createdProductId}...`);
          const orderData = { customerId: "customer123", items: [{ productId: createdProductId, quantity: 1 }] };
          const response = await axios.post('http://localhost:8080/api/orders', orderData, {
            headers: {
              'Authorization': `Bearer ${accessToken}`,
              'Content-Type': 'application/json',
            },
          });
          createdOrderId = response.data.id; // Assuming the response has an 'id' field
          setCreateOrderResult({ data: response.data, loading: false, error: null });
          console.log('API: Create Order Success:', response.data);
           if (!createdOrderId) {
            console.error('API: Create Order Error: Order ID not found in response.');
            setCreateOrderResult({ data: response.data, loading: false, error: 'Order ID not found' });
            return;
        }
        } catch (err: any) {
          setCreateOrderResult({ data: null, loading: false, error: err.message || 'Failed' });
          console.error('API: Create Order Error:', err);
          return;
        }
      }

      await delay(1000);

      // 5. Process Payment
      if (createdOrderId) {
        try {
          setProcessPaymentResult({ data: null, loading: true, error: null });
          console.log(`API: Processing payment for order ${createdOrderId}...`);
          const paymentData = { orderId: createdOrderId, amount: productPrice };
          const response = await axios.post('http://localhost:8080/api/payments', paymentData, {
            headers: {
              'Authorization': `Bearer ${accessToken}`,
              'Content-Type': 'application/json',
            },
          });
          setProcessPaymentResult({ data: response.data, loading: false, error: null });
          console.log('API: Process Payment Success:', response.data);
        } catch (err: any) {
          setProcessPaymentResult({ data: null, loading: false, error: err.message || 'Failed' });
          console.error('API: Process Payment Error:', err);
          // Not returning here, as it's the last step
        }
      }
    };

    performApiSequence();
  }, [accessToken]);

  // Helper component for consistent card styling
  const StatusCard = ({ title, loading, error, data, children }: {
    title: string;
    loading: boolean;
    error: string | null;
    data: any;
    children?: React.ReactNode;
  }) => {
    let statusText = "";
    let statusColor = "text-gray-300"; // Default/loading color

    if (loading) {
      statusText = t('loading');
      statusColor = "text-blue-400";
    } else if (error) {
      statusText = `${t('error')} ${error}`;
      statusColor = "text-red-400";
    } else if (data) {
      statusText = t('tokenSuccess'); // Using 'tokenSuccess' as a generic success message key for brevity
      statusColor = "text-green-400";
    } else if (!loading && !data && !error) {
      statusText = t('pending'); // A key for operations that haven't started (e.g. if token fails)
      statusColor = "text-yellow-400";
    }


    return (
      <div className="bg-white/10 backdrop-blur-md rounded-xl border border-white/20 shadow-lg p-6 w-full">
        <h3 className="text-xl font-semibold mb-3 text-white">{title}</h3>
        <p className={`text-sm mb-1 ${statusColor}`}>{statusText}</p>
        {data && (
          <div className="mt-2 bg-black/30 p-3 rounded-md max-h-40 overflow-y-auto">
            <pre className="text-xs text-gray-200 whitespace-pre-wrap break-all">
              {JSON.stringify(data, null, 2)}
            </pre>
          </div>
        )}
        {children} {/* For specific content like product/order ID */}
      </div>
    );
  };

  // Consolidate API states for easier mapping if desired, or handle individually
  const apiSteps = [
    { id: 'getToken', title: t('tokenStatus'), loading: tokenLoading, error: tokenError, data: accessToken ? { token: "******" } : null },
    { id: 'getAllProducts', title: t('apiGetAllProducts'), ...productsResult },
    { id: 'createProduct', title: t('apiCreateProduct'), ...createProductResult },
    { id: 'updateInventory', title: t('apiUpdateInventory'), ...updateInventoryResult },
    { id: 'createOrder', title: t('apiCreateOrder'), ...createOrderResult },
    { id: 'processPayment', title: t('apiProcessPayment'), ...processPaymentResult },
  ];


  return (
    <div className="flex flex-col items-center justify-center min-h-screen p-4 sm:p-8 font-[family-name:var(--font-geist-sans)]">
      <header className="mb-10 text-center">
        <h1 className="text-5xl font-bold text-white tracking-tight">
          {t('appName')}
        </h1>
        <p className="text-gray-300 mt-2 text-lg">{t('welcomeMessage')}</p>
      </header>

      <main className="w-full max-w-2xl space-y-6">
        {apiSteps.map((step) => (
          <StatusCard
            key={step.id}
            title={step.title}
            loading={step.loading}
            error={step.error}
            data={step.data}
          >
            {step.id === 'createProduct' && step.data && (
              <p className="text-sm text-green-300 mt-1">
                {t('successProductId', { id: (step.data as any)?.id })}
              </p>
            )}
            {step.id === 'createOrder' && step.data && (
              <p className="text-sm text-green-300 mt-1">
                {t('successOrderId', { id: (step.data as any)?.id })}
              </p>
            )}
          </StatusCard>
        ))}
      </main>

      <footer className="mt-12 text-center">
        <p className="text-sm text-gray-400">
          Inspired by VisionOS. UI by AI.
        </p>
         {/* You can add links back here if needed, or remove the original Next/Vercel footer content */}
      </footer>
    </div>
  );
}
