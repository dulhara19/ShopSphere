# Docker Setup

## Run inventory-service with dependencies

From `services/inventory-service`:

```powershell
docker compose -f infrastructure/docker/docker-compose.yml up -d --build
```

## Stop and remove containers

```powershell
docker compose -f infrastructure/docker/docker-compose.yml down
```

## Stop and remove containers + volumes

```powershell
docker compose -f infrastructure/docker/docker-compose.yml down -v
```

## Service URLs

- Inventory API: `http://localhost:3003/api`
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`
- Kafka (host listener): `localhost:29092`
