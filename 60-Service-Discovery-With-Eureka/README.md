# 60 · Service Discovery with Eureka

Services find each other by **name** through a registry (Netflix Eureka) instead of
hard-coded `host:port`. Includes client-side load balancing across **two** product-service copies.
Read **`lesson.html`** first.

| Folder             | Port        | What it shows |
|--------------------|-------------|---------------|
| `eureka-server/`   | 8761        | `@EnableEurekaServer` registry + dashboard (http://localhost:8761) |
| `product-service/` | 8081 (+8091)| Registers itself; every response says which instance (`servedBy`) answered |
| `order-service/`   | 8082        | `@LoadBalanced RestClient.Builder` calls `http://product-service/...`; `/api/orders/discovery`, `/api/orders/lb-demo` |
| `requests.http`    | –           | Sample requests (VS Code REST Client) |

## Start order (one terminal per app)

```
cd eureka-server   ; mvn spring-boot:run      # 1st - wait until it is up
cd product-service ; mvn spring-boot:run      # 2nd - instance on 8081
cd product-service ; mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8091"   # optional 2nd instance
cd order-service   ; mvn spring-boot:run      # last
```

Open http://localhost:8761 – you should see PRODUCT-SERVICE (1 or 2 instances) and ORDER-SERVICE.
Give it ~10–15 seconds after startup before calling order-service (registration + cache refresh).
Then call `GET http://localhost:8082/api/orders/lb-demo` and watch 8081 / 8091 take turns.
Stop each app with **Ctrl+C**.
