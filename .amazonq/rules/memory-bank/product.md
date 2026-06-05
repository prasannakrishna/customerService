# Product Overview — customerService

## Purpose
Customer microservice in a Supply Chain Management (SCM) platform. Manages customer lifecycle, shopping, subscriptions, payments, and order placement within an event-driven microservices ecosystem.

## Key Features
- **Customer Management**: Registration, authentication (JWT), profile CRUD, address management
- **Shopping Cart**: Add/remove items, manage shopping bag state
- **Order Processing**: Place orders triggering a Saga across Order → Inventory → Payment → Carrier → Shipment services
- **Subscriptions**: Product subscription management with status tracking
- **Payments**: Payment method management and payment processing
- **Notifications**: Customer notification storage and retrieval
- **Catalog Integration**: Proxy to catalog service for product browsing (all, search, personalized, grouped-by-community)
- **Community Integration**: Customer community membership and management
- **CQRS**: Separate command (PostgreSQL/JPA) and query (MongoDB) models

## Target Users
- End customers of the SCM platform (via API Gateway)
- Internal microservices communicating via Kafka events
- API Gateway for authentication validation (`/validate` endpoint)

## Use Cases
1. Customer registers, logs in, manages profile and addresses
2. Customer browses catalog, adds items to cart, places an order
3. Order Saga orchestrates downstream services to fulfill the order
4. Customer subscribes to products and manages payment methods
5. Query side serves read-optimized customer data from MongoDB
