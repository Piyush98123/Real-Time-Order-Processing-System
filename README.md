# Real-Time-Order-Processing-System
Real-Time Order Processing System


📌 API Contracts & Entity Classes for Real-Time Order Processing System
Below is a detailed functionality for each microservice in our Real-Time Order Processing System.

📍 Steps:
1️⃣ User places an order via the Order Service API.
2️⃣ Order Service saves it to PostgreSQL and publishes ORDER_CREATED event to Kafka.
3️⃣ Inventory Service consumes the event, checks stock, updates inventory, and publishes it to kafka.
6️⃣ Notification Service sends order updates to the customer via gmail.

// order create API
http://localhost:8095/order/api/v1/create
request Json:

{
  "userId": 12345,
"productDto": [
    {
      "productId": 1001,
      "quantity": 10,
      "productName": "Wireless Mouse",
      "category": "Electronics"
    },
    {
      "productId": 1002,
      "quantity": 1,
      "productName": "Mechanical Keyboard",
      "category": "Electronics"
    },
    {
      "productId": 1003,
      "quantity": 5,
      "productName": "USB-C Cable",
      "category": "Accessories"
    }
  ]
}

// inventory 
create product: http://localhost:8090/inventory/api/v1/create

request Json:

    [{
      "productId": 1001,
      "productName": "Wireless Mouse",
      "category": "Electronics",
      "available": 15
    },
    {
      "productId": 1002,
      "productName": "Mechanical Keyboard",
      "category": "Electronics",
      "available": 8
    },
    {
      "productId": 1003,
      "productName": "USB-C Cable",
      "category": "Accessories",
      "available": 20
    }
    ]









