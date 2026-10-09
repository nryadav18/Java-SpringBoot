# 26 · Spring Configuration, Profiles & Environment

Advanced configuration in plain Spring ("QuickCart" shop): `@Configuration` full vs lite mode
(`proxyBeanMethods`), `@Import`, `@Profile` (dev/prod beans), the `Environment`, `@PropertySource`,
custom `@Conditional` conditions (the secret behind Spring Boot auto-configuration) and SpEL `#{...}`.
Open `lesson.html` in a browser for the full lesson.

## Files (`src/main/java/com/course/configprofiles/`)
| File | What it shows |
|------|---------------|
| `App.java` | Demos 1-6; sets the default profile before `refresh()` |
| `proxydemo/FullConfig.java`, `LiteConfig.java`, `AuditLog.java`, `AuditedService.java` | Full vs lite `@Configuration` |
| `config/AppConfig.java` | `@Import`, two `@PropertySource`s (one profile-specific), `@ComponentScan` |
| `config/DevDataSourceConfig.java`, `ProdDataSourceConfig.java`, `DatabaseSettings.java` | Class-level `@Profile`, reading via `Environment` |
| `config/StorageConfig.java` + `storage/*` | Method-level `@Profile("dev")`, `@Profile("prod")`, `@Profile("!prod")` |
| `config/FeatureConfig.java` | Beans guarded by conditions |
| `condition/ConditionalOnFeature.java`, `OnFeatureCondition.java`, `OnClassPresentCondition.java` | Custom `Condition`s and a custom conditional annotation |
| `service/EnvironmentReporter.java` | Profiles, properties and property sources from the `Environment` |
| `service/PricingRules.java` | SpEL in `@Value` |
| `src/main/resources/application*.properties` | Shared, dev and prod settings |

## Run
```
cd "D:\Java-SpringBoot\26-Spring-Configuration-Profiles-And-Environment"
mvn -q compile exec:java
mvn -q compile exec:java "-Dspring.profiles.active=prod"
```
(Keep the quotes around `-D...` in PowerShell.)
