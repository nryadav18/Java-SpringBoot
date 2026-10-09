# 62 · Centralized Configuration with Spring Cloud Config Server

All services read their settings from ONE place (a Config Server backed by the
`config-repo/` folder, or a Git repo in real projects), per service and per profile,
and can reload them at runtime with `/actuator/refresh`. Read **`lesson.html`** first.

| Folder / file          | Port | What it shows |
|------------------------|------|---------------|
| `config-server/`       | 8888 | `@EnableConfigServer`, **native** profile reading `../config-repo`; `application-git.yml` shows the Git backend; `encrypt.key` enables `/encrypt` + `{cipher}` values |
| `config-repo/`         |  –   | `application.yml` (shared by all), `product-service.yml`, `product-service-dev.yml`, `product-service-prod.yml` |
| `product-service/`     | 8081 | Config client: `spring.config.import=optional:configserver:http://localhost:8888`, `ShopProperties` (@ConfigurationProperties), `MessageController` (@RefreshScope + @Value), feature flag, decrypted secret |
| `requests.http`        |  –   | Sample requests (VS Code REST Client) |

## Start order

```
cd config-server   ; mvn spring-boot:run                                   # 1st (run it FROM this folder)
cd product-service ; mvn spring-boot:run                                   # default profile
cd product-service ; mvn spring-boot:run "-Dspring-boot.run.profiles=dev"  # or: dev profile
```

Live reload: edit a value in `config-repo/product-service.yml` (or `-dev.yml`), save, then
`POST http://localhost:8081/actuator/refresh` and call `GET /api/message` again.
Without the Config Server, product-service still starts with its local fallback values (`optional:`).
Stop each app with **Ctrl+C**.
