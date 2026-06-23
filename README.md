# OrderPulse API

Real-time order status lookup microservice. Built this as a home assignment to demo a
fairly standard "service tier + database tier" setup on Kubernetes — the kind of thing
an e-commerce ops/support team would actually use to look up an order without giving
anyone direct DB access.

## What it does

- `GET /api/v1/orders` – list all orders
- `GET /api/v1/orders/{orderId}` – look up a single order
- `GET /api/v1/orders/health/info` – quick service/env info
- `GET /actuator/health`, `/actuator/health/liveness`, `/actuator/health/readiness` – used by k8s probes
- Swagger UI at `/swagger-ui.html` once it's running

Backed by Postgres, seeded with 8 sample order records via a Flyway migration
(`src/main/resources/db/migration/V1__init_orders.sql`) so there's always data to query.

## Links

- Repo: https://github.com/aranimondal/orderpulse-api
- Docker Hub image: https://hub.docker.com/r/aranimondal/orderpulse-api
- Live API (via Ingress/ALB): `http://<alb-dns-name>/api/v1/orders` — get the actual hostname with:
  ```
  kubectl get ingress orderpulse-ingress -n orderpulse
  ```
- Screen recording: `docs/demo-recording.mp4` (link added after recording — see Demo section below)

## Tech stack

- Java 21, Spring Boot 3.4.1
- Maven (see "Why Maven" below)
- Spring Data JPA + Hikari connection pooling
- Flyway for schema/versioned seed data
- PostgreSQL 16
- Docker (multi-stage build, runs as non-root)
- Kubernetes (StatefulSet for DB, Deployment + HPA + Ingress for API)
- Target platform: AWS EKS, EBS (gp3) for persistent storage, ALB for ingress

### Why Maven, not Gradle

Went with Maven here on purpose. For a single-module Spring Boot service like this with a
fairly standard dependency set, Gradle's incremental-build speed advantage doesn't really pay
off — there's no multi-module build graph to optimize, and the project doesn't have any
custom build logic that would benefit from Gradle's flexibility. Maven's declarative,
convention-heavy `pom.xml` is easier for someone else to open and understand at a glance,
which matters more here than shaving a few seconds off local build time. Also keeps CI
simpler — `mvn clean package` inside the Docker build stage, no wrapper-version
gotchas to manage. If this grows into a multi-module setup (separate API/contracts module,
shared libs, etc.) I'd reconsider, since that's where Gradle starts to actually win.

## Local run (without Docker)

```bash
# spin up a local postgres for dev
docker run --name orderpulse-local-db -e POSTGRES_DB=orderpulse \
  -e POSTGRES_USER=orderpulse_user -e POSTGRES_PASSWORD=devpass123 \
  -p 5432:5432 -d postgres:16-alpine

export DB_HOST=localhost DB_PORT=5432 DB_NAME=orderpulse
export DB_USERNAME=orderpulse_user DB_PASSWORD=devpass123

mvn clean package -DskipTests
java -jar target/orderpulse-api.jar
```

Then hit `curl http://localhost:8080/api/v1/orders`.

## Build & push the image

```bash
docker build -t aranimondal/orderpulse-api:1.0.0 .
docker login
docker push aranimondal/orderpulse-api:1.0.0
```

## Deploying to Kubernetes (AWS EKS)

Assumes you already have an EKS cluster with:
- the EBS CSI driver add-on installed (for the `orderpulse-gp3` StorageClass)
- the AWS Load Balancer Controller installed (for the ALB Ingress)
- metrics-server installed (for the HPA)

```bash
# 1. create the namespace + real secrets (password never touches a tracked YAML file)
DB_USER=orderpulse_user DB_PASSWORD='<choose-a-strong-password>' ./scripts/create-secrets.sh

# 2. apply everything else
kubectl apply -k k8s/

# 3. check rollout
kubectl get pods -n orderpulse -w
```

### Verifying it works

```bash
# all objects up?
kubectl get all -n orderpulse

# get the ALB hostname
kubectl get ingress orderpulse-ingress -n orderpulse

# hit the API
curl http://<alb-hostname>/api/v1/orders

# self-healing - kill an API pod, watch it get replaced
POD=$(kubectl get pods -n orderpulse -l app=orderpulse-api -o jsonpath='{.items[0].metadata.name}')
kubectl delete pod "$POD" -n orderpulse
kubectl get pods -n orderpulse -l app=orderpulse-api -w

# self-healing + persistence on DB side
kubectl delete pod -n orderpulse -l app=orderpulse-db
kubectl get pods -n orderpulse -l app=orderpulse-db -w
# once it's back up, call the API again - same 8 records should still be there

# rolling update demo
kubectl set image deployment/orderpulse-api orderpulse-api=aranimondal/orderpulse-api:1.0.1 -n orderpulse
kubectl rollout status deployment/orderpulse-api -n orderpulse

# HPA demo (needs a load generator, e.g. hey/k6/ab against the ALB URL)
kubectl get hpa orderpulse-api-hpa -n orderpulse -w
```

## Demo / screen recording checklist

- [ ] `kubectl get all -n orderpulse` showing every object Running
- [ ] API call returning order records
- [ ] Kill API pod → new pod scheduled automatically
- [ ] Kill DB pod → new pod scheduled, data still intact (same 8 orders)
- [ ] Rolling update of the API deployment
- [ ] HPA scaling out under load, then back down

## FinOps notes (short version — full writeup in `docs/Documentation.docx`)

- Requests/limits set per pod (200m/500m CPU, 256Mi/512Mi memory) — sized from actual
  observed usage, not guessed numbers.
- HPA min replicas kept at 4 to satisfy the requirement, but in a non-assignment, real
  cost-sensitive setup I'd drop `minReplicas` to 2 and let it scale up on demand instead
  of paying for 4 pods around the clock.
- DB sits on `gp3` EBS instead of the older `gp2` — gp3 is ~20% cheaper per GB and lets
  you tune IOPS/throughput independently instead of overpaying for storage you don't need.
- Three concrete cost levers identified and discussed in the docx: right-sizing via VPA
  recommendations, scaling down non-prod environments after hours, and moving worker
  nodes to Graviton (arm64) instances for better price-performance.

## Known limitations / things I'd do differently with more time

- No mTLS between tiers (would add a service mesh or NetworkPolicy in a real prod setup).
- Single DB replica — fine for this assignment's persistence requirement, but a real
  production Postgres would use a managed service (RDS) with Multi-AZ instead of a
  self-managed StatefulSet.
- No CI/CD pipeline wired up — built and pushed images manually for this assignment.
