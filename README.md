# Spring Boot E-Commerce API Starter

Welcome to the **Spring Boot E-Commerce API Starter**! This project is a robust, production-ready backend for an e-commerce platform, featuring user authentication, product management, shopping carts, and Stripe payment integration.

---

## 🚀 Getting Started

Follow these steps to get the project running on your local machine.

### 1. Prerequisites
Ensure you have the following installed:
- **Java 17 or higher** (JDK 17+)
- **MySQL Server**
- **Stripe CLI** (for testing payments locally)
- **Postman** (recommended for testing endpoints)

### 2. Database Setup
1. Open your MySQL terminal or a tool like MySQL Workbench.
2. Create a new database named `store_api`:
   ```sql
   CREATE DATABASE store_api;
   ```

### 3. Environment Configuration
Create a file named `.env` in the root directory of the project (if it doesn't exist) and fill in your secrets. You can use the provided `.env.example` as a template.

```properties
JWT_SECRET=your_super_secret_jwt_key_here
STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key
STRIPE_WEBHOOK_SECRET_KEY=whsec_your_stripe_webhook_secret
```
> [!TIP]
> You can get your Stripe keys from the [Stripe Dashboard](https://dashboard.stripe.com/test/apikeys).

### 4. Run the Application
Open your terminal in the project root and run:
```bash
./mvnw spring-boot:run
```
The server will start on `http://localhost:8080`.

---

## 🛠 Testing Endpoints Step-by-Step

Here is how to test the core features of the API.

### Phase 1: Authentication & Users
First, you need to create a user and log in to get an **Access Token**.

1.  **Register a User**
    - **Endpoint**: `POST /users`
    - **Body (JSON)**:
      ```json
      {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john@example.com",
        "password": "password123"
      }
      ```
2.  **Login**
    - **Endpoint**: `POST /auth/login`
    - **Body (JSON)**:
      ```json
      {
        "email": "john@example.com",
        "password": "password123"
      }
      ```
    - **Note**: Copy the `accessToken` from the response. You will need it for protected endpoints.

### Phase 2: Products
1.  **Get All Products**
    - **Endpoint**: `GET /products` (Public)
2.  **Get Product by ID**
    - **Endpoint**: `GET /products/{id}` (Public)

### Phase 3: Shopping Cart
1.  **Create a Cart**
    - **Endpoint**: `POST /carts`
    - **Note**: Copy the `id` (UUID) of the cart created.
2.  **Add Product to Cart**
    - **Endpoint**: `POST /carts/{cartId}/items`
    - **Body (JSON)**:
      ```json
      {
        "productId": 1
      }
      ```
3.  **View Cart**
    - **Endpoint**: `GET /carts/{cartId}`

### Phase 4: Checkout & Payments (Stripe)
1.  **Initialize Checkout**
    - **Endpoint**: `POST /checkout`
    - **Headers**: `Authorization: Bearer <YOUR_ACCESS_TOKEN>`
    - **Body (JSON)**:
      ```json
      {
        "cartId": "YOUR_CART_UUID"
      }
      ```
    - **Result**: You will get a `checkoutUrl`. Open this in your browser to complete the payment using a test card.

2.  **Testing Webhooks Locally**
    - Open a new terminal and run the Stripe CLI:
      ```bash
      stripe listen --forward-to localhost:8080/checkout/webhook
      ```
    - Note the `whsec_...` key provided and update your `.env` file if it differs.
    - Trigger a successful payment:
      ```bash
      stripe trigger checkout.session.completed --add checkout_session:metadata.order_id=YOUR_ORDER_ID
      ```

---

## 📖 API Documentation
You can view the full interactive API documentation (Swagger) at:
`http://localhost:8080/swagger-ui.html`

## 🎓 Tips for Students
- **Status Codes**: Look for `200 OK`, `201 Created`, or `401 Unauthorized` to understand if your request worked.
- **JSON Format**: Always ensure your request body is valid JSON.
- **Logs**: Check the terminal running Spring Boot for detailed error messages if something goes wrong.

Happy Coding! 🚀
