# Payment Service

**Enterprise-Grade Payment Processing Microservice with Stripe Integration**

## Overview

The Payment Service is a Spring Boot microservice responsible for handling all payment processing, transaction management, and payment method management in the ShopSphere platform. It integrates with Stripe for secure payment processing and supports multiple payment methods.

**Service Port:** 3005  
**Owner:** Team Member 5  
**Tech Stack:** Spring Boot 3.1, Spring Data JPA, PostgreSQL, Stripe SDK

## Phase 1 - MVP (Core Features)

### Epic 1.1: Stripe Integration Setup ✅
- **Priority:** Critical
- **Status:** Implemented
- Configure Stripe SDK
- Create Stripe customer on user registration
- Webhook endpoint setup with signature verification
- Error handling with retry logic

### Epic 1.2: Payment Intent Flow ✅
- **Priority:** Critical
- **Status:** Implemented
- Create payment intent
- Confirm payment
- Handle payment failure with retry capability
- Payment status check with Stripe sync

### Epic 1.3: Card Payment Processing ✅
- **Priority:** Critical
- **Status:** Implemented
- Save card for future use (tokenization)
- List saved cards
- Pay with saved card
- Remove saved card
- Set default card

### Epic 1.4: Refund Processing ✅
- **Priority:** High
- **Status:** Implemented
- Full refund
- Partial refund
- Refund status tracking
- Refund webhook handling

### Epic 1.5: Transaction History ✅
- **Priority:** High
- **Status:** Implemented
- List user transactions (paginated)
- Transaction details
- Admin transaction view
- Transaction export to CSV

### Epic 1.6: Internal Service Communication ✅
- **Priority:** High
- **Status:** Implemented
- Payment status callback to Order Service
- Get payment for order
- Payment events publishing

## Project Structure

```
payment-service/
├── src/
│   ├── main/
│   │   ├── java/com/shopsphere/payment/
│   │   │   ├── PaymentServiceApplication.java   # Main app class
│   │   │   ├── config/
│   │   │   │   ├── StripeConfiguration.java     # Stripe SDK config
│   │   │   │   ├── RestClientConfiguration.java # REST client config
│   │   │   │   └── EventPublisherConfiguration.java
│   │   │   ├── controller/
│   │   │   │   ├── PaymentController.java       # Payment operations
│   │   │   │   ├── PaymentMethodController.java # Card management
│   │   │   │   ├── RefundController.java        # Refund operations
│   │   │   │   ├── WebhookController.java       # Stripe webhooks
│   │   │   │   ├── InternalPaymentController.java # Internal APIs
│   │   │   │   └── TransactionController.java   # Transaction history
│   │   │   ├── dto/
│   │   │   │   ├── CreatePaymentIntentRequest.java
│   │   │   │   ├── CreatePaymentIntentResponse.java
│   │   │   │   ├── PaymentStatusResponse.java
│   │   │   │   ├── ConfirmPaymentRequest.java
│   │   │   │   ├── CardPaymentMethodRequest.java
│   │   │   │   ├── PaymentMethodResponse.java
│   │   │   │   ├── RefundRequest.java
│   │   │   │   ├── RefundResponse.java
│   │   │   │   └── TransactionResponse.java
│   │   │   ├── event/
│   │   │   │   ├── PaymentSucceededEvent.java
│   │   │   │   ├── PaymentFailedEvent.java
│   │   │   │   └── RefundCompletedEvent.java
│   │   │   ├── exception/
│   │   │   │   ├── PaymentException.java
│   │   │   │   ├── PaymentNotFoundException.java
│   │   │   │   ├── StripeApiException.java
│   │   │   │   └── InsufficientFundsException.java
│   │   │   ├── model/
│   │   │   │   ├── Payment.java                 # Payment entity
│   │   │   │   ├── Refund.java                  # Refund entity
│   │   │   │   └── PaymentMethodEntity.java     # Saved card entity
│   │   │   ├── repository/
│   │   │   │   ├── PaymentRepository.java       # Payment persistence
│   │   │   │   ├── RefundRepository.java        # Refund persistence
│   │   │   │   └── PaymentMethodRepository.java # Card persistence
│   │   │   └── service/
│   │   │       ├── PaymentService.java          # Payment business logic
│   │   │       ├── StripeService.java           # Stripe API wrapper
│   │   │       ├── RefundService.java           # Refund business logic
│   │   │       ├── PaymentMethodService.java    # Card business logic
│   │   │       └── WebhookService.java          # Webhook handling
│   │   └── resources/
│   │       ├── application.yml                  # Default config
│   │       ├── application-prod.yml             # Production config
│   │       └── db/migration/
│   │           └── V1__Create_payments_table.sql
│   └── test/
│       └── java/com/shopsphere/payment/
├── pom.xml
├── README.md
└── docs/EPICS.md
```

## Database Schema

### Payments Table
- `id` (UUID): Primary key
- `order_id` (VARCHAR): Order reference (unique)
- `user_id` (VARCHAR): User reference
- `stripe_payment_intent_id` (VARCHAR): Stripe PaymentIntent ID
- `stripe_customer_id` (VARCHAR): Stripe Customer ID
- `amount` (DECIMAL): Payment amount
- `currency` (VARCHAR): Currency code (e.g., USD)
- `status` (VARCHAR): PENDING, PROCESSING, SUCCEEDED, FAILED, CANCELLED
- `payment_method` (VARCHAR): CARD, PAYPAL, CRYPTO, BANK_TRANSFER
- `failure_reason` (TEXT): Error message if failed
- `metadata` (JSONB): Additional data
- `created_at` (TIMESTAMP): Creation time
- `updated_at` (TIMESTAMP): Last update time

### Refunds Table
- `id` (UUID): Primary key
- `payment_id` (UUID): Foreign key to payments
- `stripe_refund_id` (VARCHAR): Stripe Refund ID
- `amount` (DECIMAL): Refund amount
- `reason` (VARCHAR): CUSTOMER_REQUEST, DUPLICATE, FRAUDULENT, OTHER
- `status` (VARCHAR): PENDING, PROCESSING, SUCCEEDED, FAILED
- `failure_reason` (TEXT): Error message if failed
- `created_at` (TIMESTAMP): Creation time
- `updated_at` (TIMESTAMP): Last update time

### Payment Methods Table
- `id` (UUID): Primary key
- `user_id` (VARCHAR): User reference
- `stripe_payment_method_id` (VARCHAR): Stripe Payment Method ID (unique)
- `card_brand` (VARCHAR): Card brand (Visa, MasterCard, etc.)
- `last_four_digits` (VARCHAR): Last 4 digits of card
- `expiry_month` (INTEGER): Card expiry month
- `expiry_year` (INTEGER): Card expiry year
- `is_default` (BOOLEAN): Whether it's the default card
- `created_at` (TIMESTAMP): Creation time
- `updated_at` (TIMESTAMP): Last update time

## API Endpoints

### Payment Operations
```
POST   /api/payments/create-intent      Create payment intent
POST   /api/payments/{id}/confirm       Confirm payment
POST   /api/payments/{id}/retry         Retry failed payment
GET    /api/payments/{id}/status        Get payment status
```

### Payment Methods (Cards)
```
GET    /api/payment-methods             List saved cards
POST   /api/payment-methods             Save new card
DELETE /api/payment-methods/{id}        Delete saved card
PUT    /api/payment-methods/{id}/default Set default card
```

### Refunds
```
POST   /api/payments/{id}/refund        Process refund
GET    /api/payments/{id}/refunds       List payment refunds
GET    /api/refunds/{refundId}          Get refund details
```

### Transactions
```
GET    /api/transactions                List user transactions
GET    /api/transactions/{id}           Get transaction details
GET    /api/transactions/export         Export to CSV
```

### Webhooks
```
POST   /api/webhooks/stripe             Receive Stripe webhooks
```

### Internal Endpoints (Service-to-Service)
```
GET    /internal/payments/order/{orderId}         Get payment by order
POST   /internal/payments/{id}/status-callback    Payment status callback
```

## Getting Started

### Prerequisites
- Java 17+
- PostgreSQL 12+
- Maven 3.8+
- Stripe API Keys (test/live)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/shopsphere/payment-service.git
   cd payment-service
   ```

2. **Configure environment variables**
   ```bash
   export STRIPE_API_KEY=sk_test_xxx
   export STRIPE_WEBHOOK_SECRET=whsec_xxx
   export DB_USERNAME=postgres
   export DB_PASSWORD=postgres
   ```

3. **Create database**
   ```bash
   createdb payment_db
   ```

4. **Build the project**
   ```bash
   mvn clean package
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   Or:
   ```bash
   java -jar target/payment-service-1.0.0.jar
   ```

The service will start on `http://localhost:3005`

### Docker Setup

```bash
docker build -t shopsphere/payment-service:latest .
docker run -p 3005:3005 \
  -e STRIPE_API_KEY=sk_test_xxx \
  -e STRIPE_WEBHOOK_SECRET=whsec_xxx \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  shopsphere/payment-service:latest
```

## Configuration

### application.yml

Key configuration properties:

```yaml
stripe:
  api-key: ${STRIPE_API_KEY}           # Stripe API key
  webhook-secret: ${STRIPE_WEBHOOK_SECRET}

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/payment_db
    username: postgres
    password: postgres
```

### Environment Variables

- `STRIPE_API_KEY`: Stripe secret API key
- `STRIPE_WEBHOOK_SECRET`: Stripe webhook signing secret
- `DB_USERNAME`: PostgreSQL username
- `DB_PASSWORD`: PostgreSQL password

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Manual Testing

1. **Create Payment Intent**
   ```bash
   curl -X POST http://localhost:3005/api/payments/create-intent \
     -H "Content-Type: application/json" \
     -d '{
       "orderId": "order-123",
       "userId": "user-456",
       "amount": 99.99,
       "currency": "usd"
     }'
   ```

2. **Get Payment Status**
   ```bash
   curl http://localhost:3005/api/payments/{paymentId}/status
   ```

3. **Process Refund**
   ```bash
   curl -X POST http://localhost:3005/api/payments/{paymentId}/refund \
     -H "Content-Type: application/json" \
     -d '{
       "amount": 50.00,
       "reason": "CUSTOMER_REQUEST"
     }'
   ```

## API Documentation

Swagger/OpenAPI documentation is available at:
- **Swagger UI**: http://localhost:3005/swagger-ui.html
- **OpenAPI JSON**: http://localhost:3005/v3/api-docs

## Integration with Other Services

### Order Service
- Payment Service notifies Order Service when payment succeeds/fails
- Order Service can query payment status via internal endpoint

### User Service
- Stripe customers are created when users register

### Notification Service
- Payment events can trigger notifications (email, SMS)

## Monitoring & Observability

### Health Check
```bash
curl http://localhost:3005/actuator/health
```

### Metrics
```bash
curl http://localhost:3005/actuator/metrics
```

## Error Handling

The service implements comprehensive error handling:

- **PaymentException**: Base exception for payment errors
- **PaymentNotFoundException**: Payment not found in database
- **StripeApiException**: Stripe API errors
- **InsufficientFundsException**: Refund exceeds payment amount

## Security Considerations

- **PCI DSS Compliance**: Never store raw card data (Stripe handles tokenization)
- **Webhook Signature Verification**: All Stripe webhooks are verified
- **HTTPS Only**: All external communication is encrypted
- **Rate Limiting**: Implemented via API Gateway

## Deployment

### Kubernetes
```bash
kubectl apply -f kubernetes/payment-service-deployment.yaml
kubectl apply -f kubernetes/payment-service-service.yaml
```

### AWS ECS
```bash
./scripts/deploy-ecs.sh payment-service
```

## Troubleshooting

### Database Connection Issues
```
java.sql.SQLRecoverableException: IO Error: Connection refused
```
- Ensure PostgreSQL is running
- Check database credentials in application.yml

### Stripe API Errors
- Verify API key and webhook secret
- Check Stripe dashboard for rate limits
- Review error logs for specific Stripe error codes

### Webhook Issues
- Verify webhook secret is correct
- Check firewall allows inbound traffic on 3005
- Review Stripe webhook delivery logs

## Related Documentation

- [EPICS.md](docs/EPICS.md) - Detailed epic breakdown
- [Stripe API Documentation](https://stripe.com/docs/api)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## License

This project is part of ShopSphere and is licensed under the MIT License.

## Support

For issues or questions about the Payment Service:
1. Check this README and EPICS.md
2. Review existing GitHub issues
3. Contact Team Member 5 (Payment Service Owner)
