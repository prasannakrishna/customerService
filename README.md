# customerService
# End-to-End Flow for Customer Order Processing

## Overview
This application demonstrates an end-to-end flow for processing customer orders using a Saga pattern. The flow is orchestrated using Axon Framework with event sourcing and distributed services, ensuring scalability and fault tolerance.

## Flow Description
1. **API Gateway**
    - All incoming requests are routed through the API Gateway.
    - Requests are validated by the `authentication service` through the `/validate` endpoint.
    - Upon successful validation, the requests are redirected to their respective services.

2. **Place Customer Order API**
    - The `placeCustomerOrder` API initiates the Saga for processing the order.
    - The Saga orchestrates the following sequential actions:
        - **Create Order**
        - **Create Order Line**
        - **Allocate Inventory**
        - **Make Payment**
        - **Assign Carrier**
        - **Create Shipment**

3. **Axon Server**
    - Events are handled via the Axon Event Bus.
    - The Axon Server orchestrates event-based communication between services, ensuring eventual consistency.
    - Axon Server is deployed using Docker Compose on managed servers for scalability.

4. **Eureka Service Registry**
    - Eureka is used for service discovery and to register the `authentication service`.
    - It also facilitates proxy redirection to various microservices.

## Managed Services
The application consists of the following managed services:
- **Authentication Service**
- **Order Service**
- **Inventory Service**
- **Payment Service**
- **Carrier Service**
- **Shipment Service**

## Deployment
### Docker Compose
The Axon Server, Eureka Service Registry, API Gateway, and microservices are configured to run on managed servers using Docker Compose. This ensures seamless orchestration and service scaling.

---

This application is designed to handle high traffic and maintain robustness with event-driven microservices architecture. It leverages the following tools:
- **Axon Framework**: For event sourcing and Saga orchestration.
- **API Gateway**: For request routing and authentication.
- **Eureka**: For service discovery and proxying.
