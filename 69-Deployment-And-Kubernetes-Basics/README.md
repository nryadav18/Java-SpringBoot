# 69 - Deployment & Kubernetes Basics

`menu-service`: a small Spring Boot app made production-ready (probes, graceful shutdown,
config from env vars, JSON logs) and deployed to Kubernetes. Open `lesson.html` for the lesson.

## Files
| File | What it shows |
|------|---------------|
| `src/main/java/.../menu/MenuController.java` | `/api/menu`, `/api/info` (which pod answered), `/api/slow` (graceful shutdown demo) |
| `src/main/java/.../ops/ReadinessController.java` | Switch readiness off/on to watch Kubernetes remove a pod from the Service |
| `src/main/java/.../config/AppProperties.java` | `app.*` settings from ConfigMap/Secret env vars; secret is masked |
| `application.properties` / `application-prod.properties` | Graceful shutdown, probes, prod JSON logs |
| `src/test/.../ProbesTest.java` | Tests liveness/readiness endpoints |
| `Dockerfile` | Multi-stage image, numeric non-root user |
| `k8s/configmap.yaml`, `k8s/secret.yaml` | Configuration and secrets as env vars |
| `k8s/deployment.yaml` | 2 replicas, rolling update, resources, startup/liveness/readiness probes, preStop |
| `k8s/service.yaml` | Stable ClusterIP address + load balancing |
| `k8s/ingress.yaml` | Optional HTTP routing (needs an ingress controller) |
| `deploy/menu-service.service`, `deploy/menu-service.env.example` | Running the jar on a Linux VM with systemd |

## Run
```powershell
mvn spring-boot:run                       # http://localhost:8080/api/info
docker build -t menu-service:1.0.0 .      # image for the cluster
kubectl apply -f k8s/                     # Docker Desktop Kubernetes / minikube / kind
kubectl get pods,svc
kubectl port-forward service/menu-service 8080:80
kubectl delete -f k8s/                    # clean up
```
