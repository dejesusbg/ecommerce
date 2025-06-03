# Frontend Service

This service provides a simple Next.js frontend to interact with the API gateway, demonstrating a sequence of API calls after obtaining a JWT from Keycloak.

## Configuration

### For Local Development

1.  Navigate to the `frontend-service` directory.
2.  Create a `.env.local` file by copying the example:
    ```bash
    cp .env.local.example .env.local
    ```
3.  Edit `.env.local` and set your Keycloak client secret:
    ```
    NEXT_PUBLIC_KEYCLOAK_CLIENT_SECRET=your_actual_client_secret
    ```

### For Docker Compose

When running via the main `docker-compose.yml` at the project root:

1.  Ensure you have a `.env` file in the project root directory (next to `docker-compose.yml`).
2.  Add or update the following variable in that root `.env` file:
    ```
    KEYCLOAK_CLIENT_SECRET_FRONTEND=your_actual_client_secret
    ```
    If this variable is not set, a default test secret will be used (not recommended for actual use).

## Running the Application

### Local Development

1.  Navigate to the `frontend-service` directory.
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Run the development server:
    ```bash
    npm run dev
    ```
    The application will be available at `http://localhost:3000`.

### With Docker Compose

1.  From the project root directory:
    ```bash
    docker-compose up --build frontend-service
    ```
    If you want to run all services:
    ```bash
    docker-compose up --build
    ```
    The application will be available at `http://localhost:3000`.

## Internationalization (i18n)

The UI supports English (`en`) and Spanish (Colombia - `es-CO`).
Locale files are located in `public/locales/`.
