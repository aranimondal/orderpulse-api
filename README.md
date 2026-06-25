# OrderPulse API

Order status lookup microservice built with Java 21 and Spring Boot 3.4. This project demonstrates a two-tier architecture (Service API + Database) deployed on Kubernetes, where the API tier fetches order records from a PostgreSQL database and exposes them over REST endpoints.

## Links

| Item | URL |
|------|-----|
| Code Repository | https://github.com/aranimondal/orderpulse-api |
| Docker Hub Image | https://hub.docker.com/r/aranimondal/orderpulse-api/tags |
| Service API Tier (Live) | http://k8s-orderpul-orderpul-d0ee8748bb-1588443580.ap-south-1.elb.amazonaws.com/api/v1/orders |
| Screen Recording | `docs/demo-recording.mp4` |

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/orders` | Fetch all orders from database |
| GET | `/api/v1/orders/{orderId}` | Fetch a single order by ID |
| GET | `/api/v1/orders/health/info` | Service health and environment info |
| GET | `/actuator/health` | Kubernetes liveness/readiness probe endpoint |

## Tech Stack

- Java 21, Spring Boot 3.4.1, Maven
- Spring Data JPA with HikariCP connection pooling
- Flyway for database schema migration and seed data
- PostgreSQL 16 (database tier)
- Docker (multi-stage build, non-root container)
- Kubernetes on AWS EKS (Deployment, StatefulSet, HPA, Ingress, ConfigMap, Secrets)
- AWS EBS gp3 for persistent storage, ALB for external ingress

## Project Structure

```
├── src/main/java          # Application source code
├── src/main/resources     # application.yml + Flyway migrations
├── src/test/java          # Unit and integration tests
├── k8s/                   # All Kubernetes manifest files
├── scripts/               # Helper scripts (secret creation)
├── docs/                  # Documentation and screen recording
├── Dockerfile             # Multi-stage Docker build
└── pom.xml                # Maven build configuration
```

## Kubernetes Architecture

```
                    ┌─────────────────────────────────┐
                    │        AWS ALB (Ingress)         │
                    │   internet-facing, port 80       │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────▼──────────────────┐
                    │   orderpulse-api (Deployment)    │
                    │   4 replicas, rolling updates    │
                    │   ConfigMap + Secret driven      │
                    │   HPA: 4-10 pods on CPU/memory   │
                    └──────────────┬──────────────────┘
                                   │ ClusterIP Service (DNS)
                    ┌──────────────▼──────────────────┐
                    │   orderpulse-db (StatefulSet)    │
                    │   1 replica, PVC on EBS gp3      │
                    │   ClusterIP only (not exposed)   │
                    │   8 seed records via Flyway      │
                    └─────────────────────────────────┘
```

## How to Run Locally

Requires Java 21 and a running PostgreSQL instance.

```bash
# Create the database (one-time)
psql -U postgres -c "CREATE DATABASE orderpulse;"

# Build
mvn clean package -DskipTests

# Run (pass DB credentials as environment variables)
java -Dspring.datasource.url=jdbc:postgresql://localhost:5432/orderpulse \
     -Dspring.datasource.username=postgres \
     -Dspring.datasource.password=<your-password> \
     -jar target/orderpulse-api.jar
```

Flyway automatically creates the `orders` table and inserts 8 sample records on first startup.

## Docker

```bash
docker build -t aranimondal/orderpulse-api:1.0.0 .
docker login
docker push aranimondal/orderpulse-api:1.0.0
```

The image is also built and pushed automatically via GitHub Actions on every push to `main`.

## Deploying to Kubernetes (EKS)

Prerequisites on the EKS cluster:
- EBS CSI driver (for PersistentVolume provisioning)
- AWS Load Balancer Controller (for ALB Ingress)
- metrics-server (for HPA)

```bash
# Create secrets (password never stored in any tracked YAML)
kubectl create namespace orderpulse
kubectl create secret generic orderpulse-db-secret --namespace orderpulse \
  --from-literal=POSTGRES_USER=orderpulse_user \
  --from-literal=POSTGRES_PASSWORD='<strong-password>'
kubectl create secret generic orderpulse-api-secret --namespace orderpulse \
  --from-literal=DB_USERNAME=orderpulse_user \
  --from-literal=DB_PASSWORD='<strong-password>'

# Deploy all resources
kubectl apply -k k8s/

# Verify
kubectl get all -n orderpulse
kubectl get ingress -n orderpulse
```

## Kubernetes Manifests (k8s/ directory)

| File | Purpose |
|------|---------|
| `00-namespace.yaml` | Namespace isolation |
| `01-configmap.yaml` | DB host, port, pool config (externalized from code) |
| `02-secret.template.yaml` | Template showing secret structure (no real values) |
| `03-storageclass.yaml` | EBS gp3 StorageClass for DB persistence |
| `04-db-statefulset.yaml` | PostgreSQL StatefulSet + ClusterIP Services |
| `05-api-deployment.yaml` | API Deployment (4 replicas, rolling update, probes) |
| `06-api-service.yaml` | ClusterIP Service for the API tier |
| `07-api-hpa.yaml` | HorizontalPodAutoscaler (CPU 65%, memory 75%) |
| `08-ingress.yaml` | ALB Ingress for external access |

## FinOps Considerations

- CPU and memory requests/limits are defined for both tiers, sized based on observed `kubectl top` metrics rather than arbitrary numbers.
- Three cost optimization opportunities identified:
  1. Right-sizing resource requests from actual pod metrics (implemented)
  2. Reducing HPA `minReplicas` during off-peak hours
  3. Using AWS Graviton (arm64) instances for worker nodes (~20-40% better price-performance)
- Database uses gp3 EBS volumes which decouple IOPS from storage size, avoiding over-provisioning.

Full FinOps analysis is in `docs/Documentation.docx`.

## Screen Recording Demo Checklist

The recording demonstrates:
- All Kubernetes objects deployed and running
- API call retrieving order records from the database tier
- Killing an API pod and showing it auto-regenerates (self-healing)
- Killing the database pod and showing it regenerates with data intact (persistence)
- Rolling update deployment strategy
- HPA configuration and resource metrics
