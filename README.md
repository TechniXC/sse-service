# SSE-service

**SSE-service** is a backend service designed to provide Server-Sent Events (SSE) support and messaging capabilities.  
It integrates with Redis and RabbitMQ as core infrastructure components and offers optional observability features such as logging with Loki and tracing with Tempo.

---

## 🚀 Requirements

To run the project, make sure the following infrastructure is available:

- **Java**: `>= 17`
- **Redis**: accessible at `redis:6379` (no password)
- **RabbitMQ**: accessible at `rabbitmq:5672` (with credentials `user/password`)

---

## ⚙️ Optional Integrations

- **Logging**: [Grafana Loki](https://grafana.com/oss/loki/) at `loki:3100`
- **Tracing**: [Grafana Tempo](https://grafana.com/oss/tempo/) (via Zipkin protocol) at `tempo:9411`