# Inventory Service API Endpoints

## Base
- Local base URL: `http://localhost:3003`
- Context path: `/api`
- Effective API base: `http://localhost:3003/api`

## Health
- `GET /api/actuator/health`

## OpenAPI / Swagger
- `GET /api/v3/api-docs`
- `GET /api/swagger-ui/index.html`

## Inventory (Core)
- `POST /api/inventory`
- `GET /api/inventory/{productId}`
- `PUT /api/inventory/{productId}`
- `POST /api/inventory/bulk-update`
- `DELETE /api/inventory/{productId}`

## Reservation
- `POST /api/inventory/reserve`
- `POST /api/inventory/confirm`
- `POST /api/inventory/release`
- `POST /api/inventory/check-availability`

## Low Stock
- `PUT /api/inventory/{productId}/threshold?threshold={value}`
- `GET /api/inventory/low-stock`
- `GET /api/inventory/out-of-stock`

## Reservation Monitoring
- `GET /api/inventory/{productId}/reservations`
- `GET /api/inventory/order/{orderId}/reservations`

## Stock History and Audit
- `GET /api/inventory/{productId}/history`
- `POST /api/inventory/{productId}/adjustment`
- `GET /api/inventory/audit-report`

## Real-Time Stream
- `GET /api/inventory/stream` (SSE)

## Internal APIs
- `GET /api/internal/inventory/{productId}`
- `POST /api/internal/inventory/batch`
- `GET /api/internal/inventory/{productId}/available/{quantity}`

## Warehouse APIs
- `POST /api/warehouses`
- `GET /api/warehouses`
- `GET /api/warehouses/{warehouseId}`
- `PUT /api/warehouses/{warehouseId}`
- `POST /api/inventory/{productId}/warehouses/{warehouseId}`
- `GET /api/inventory/{productId}/warehouses`
- `POST /api/inventory/transfer`

## Analytics and Forecast
- `GET /api/inventory/analytics/turnover`
- `GET /api/inventory/analytics/days-remaining`
- `GET /api/inventory/analytics/dead-stock`
- `GET /api/inventory/analytics/valuation`
- `GET /api/inventory/{productId}/restock-recommendation`
- `GET /api/inventory/forecasts`

## Integration
- `POST /api/inventory/import` (multipart/form-data)
- `GET /api/inventory/export` (text/csv)
- `POST /api/inventory/webhooks`

## Note
- `/api` root endpoint has no mapping, so direct `GET /api` returns `404` by design.
