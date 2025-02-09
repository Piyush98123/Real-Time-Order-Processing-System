# Real-Time-Order-Processing-System
Real-Time Order Processing System


📌 API Contracts & Entity Classes for Real-Time Order Processing System
Below is a detailed functionality for each microservice in our Real-Time Order Processing System.


📌 1. Order Creation & Processing Flow
📍 Steps:
1️⃣ User places an order via the Order Service API.
2️⃣ Order Service saves it to PostgreSQL and publishes ORDER_CREATED event to Kafka.
3️⃣ Inventory Service consumes the event, checks stock, updates inventory, and publishes INVENTORY_UPDATED.
4️⃣ Payment Service processes payment and publishes PAYMENT_SUCCESS or PAYMENT_FAILED.
5️⃣ If payment is successful, Shipping Service assigns a delivery agent and publishes ORDER_SHIPPED.
6️⃣ Notification Service sends order updates to the customer.


📌 Order Cancellation & Payment Failure Flow
📍 Steps:
1️⃣ User cancels an order via the Order Service API.
2️⃣ Order Service updates the order status to "CANCELLED" and publishes ORDER_CANCELLED event.
3️⃣ Inventory Service listens to ORDER_CANCELLED and restores stock.
4️⃣ Payment Service listens to ORDER_CANCELLED and processes a refund (if payment was made).
5️⃣ If payment fails initially, Payment Service publishes PAYMENT_FAILED, and Order Service marks order as "FAILED".
6️⃣ Notification Service listens to both ORDER_CANCELLED and PAYMENT_FAILED to inform the user.


📌 Order Shipment Flow
📍 Steps:
1️⃣ After a successful payment, Payment Service publishes PAYMENT_SUCCESS event.
2️⃣ Shipping Service listens to PAYMENT_SUCCESS, assigns a delivery agent, and updates order status to "IN_TRANSIT".
3️⃣ Shipping Service publishes ORDER_SHIPPED event.
4️⃣ Notification Service listens to ORDER_SHIPPED and sends tracking updates to the user.
5️⃣ Once the order is delivered, Shipping Service updates the status to "DELIVERED" and publishes ORDER_DELIVERED.
6️⃣ Notification Service sends a final delivery confirmation to the user.


📌 User Registration Flow
📍 Steps:
1️⃣ User submits registration details via the User Service API.
2️⃣ User Service validates the data and stores the user details in PostgreSQL.
3️⃣ User Service publishes a USER_REGISTERED event to Kafka.
4️⃣ Notification Service listens to USER_REGISTERED and sends a welcome email/SMS to the user.
5️⃣ If an error occurs, User Service returns a failure response to the user.
