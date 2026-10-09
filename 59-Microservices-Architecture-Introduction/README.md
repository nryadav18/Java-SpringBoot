# 59 · Microservices Architecture – Introduction

A mostly **conceptual** topic: monolith vs microservices, bounded contexts, sync vs async
communication, database per service, the 12-factor app and the key patterns map.
Open **`lesson.html`** in your browser first.

The code is a tiny 2-service system that works, but shows the problems the next topics solve.

| Folder / file       | Port | What it shows |
|---------------------|------|---------------|
| `product-service/`  | 8081 | Owns product data. `GET /api/products`, `GET /api/products/{id}` |
| `order-service/`    | 8082 | Owns orders. `POST /api/orders` calls product-service over HTTP with **RestClient** and a **hard-coded URL** (`product-service.base-url` in application.properties) |
| `requests.http`     |  –   | Sample requests for the VS Code REST Client extension |

## Start order (two terminals)

```
cd product-service
mvn spring-boot:run          # 1st  -> http://localhost:8081/api/products

cd order-service
mvn spring-boot:run          # 2nd  -> http://localhost:8082/api/orders
```

Then try `requests.http`. Stop product-service (Ctrl+C) and place an order again:
you get **503 Service Unavailable** – that is the "network can fail" lesson.
Stop each app with **Ctrl+C**.
