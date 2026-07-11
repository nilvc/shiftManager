# Deployment and Rollback Strategy

## Scope

Core runtime components:

- Spring Boot API and static frontend served on port `8080`
- JPA persistence
- Actuator dependency already present
- Current local database: file-based H2 under `./data`

For production, replace H2 with a managed database such as PostgreSQL or MySQL. H2 is used currently for local development and demos, but it is not a strong production database choice for multi-instance deployments, backups, scaling, or operational reliability.

## Deployment Strategy

### 1. Environments

Use separate environments with separate configuration and data:

- `local`: developer machine, H2 file database
- `dev`: shared integration environment
- `staging`: production-like validation environment
- `prod`: customer/user-facing environment

Configuration should come from environment variables or platform secrets, not hardcoded properties. We can use a secret management service to store and pass sensitive values during deployment instead of hardcoding them in code. 
EG - SMS Storefront serive.


Use database migrations with Flyway or Liquibase before production rollout. Avoid `spring.jpa.hibernate.ddl-auto=update` in production because automatic schema changes can be hard to review and roll back.

### 2. Build

Each release should produce an immutable, versioned image.
Once a release build version is created it cannot be modified as we want same version used for UAT testing in preprod regions to be deployed to Prod.


### 3. Pre-Deployment Gates

Before deploying to staging or production:

- Build succeeds.
- Unit and integration tests pass.
- Container starts successfully.
- `/actuator/health` returns `UP`.
- Database migration is reviewed and tested.
- Security-sensitive settings are not committed, especially database credentials.

### 4. Deployment Pattern

Use rolling deployment for normal low-risk changes:

1. Deploy the new image to one instance or a small percentage of instances.
2. Wait for readiness checks to pass.
3. Route traffic to the new instance.
4. Continue replacing old instances gradually.
5. Monitor errors, latency, CPU, memory, and business metrics during rollout.

For higher-risk releases, use blue-green or canary deployment:

- `blue`: current stable version
- `green`: new version
- Send a small amount of traffic to `green`.
- Increase traffic gradually when metrics remain healthy.
- Switch all traffic to `green` after validation.
- Keep `blue` available for fast rollback.

### 5. Health Checks

Use Spring Boot Actuator health endpoints:

- Liveness: `/actuator/health/liveness`
- Readiness: `/actuator/health/readiness`
- General health: `/actuator/health`

Readiness should fail if the app cannot serve traffic, for example when the database is unavailable.

## Rollback Strategy

### Rollback Triggers

Rollback immediately if any of these happen after deployment:

- `/actuator/health` is not `UP`.
- Error rate is above the agreed threshold for 5 to 10 minutes.
- P95 or P99 latency increases significantly from baseline.
- Shift creation, shift lookup, or shift swap APIs fail repeatedly.
- Database migration causes data corruption or query failures.
- CPU or memory remains saturated after scaling.
- Users cannot approve or reject shift swap requests.

Suggested initial thresholds:

- HTTP 5xx rate greater than `2%` for 5 minutes
- P95 API latency greater than `1 second` for 10 minutes
- JVM memory usage above `85%` for 10 minutes
- CPU usage above `80%` for 10 minutes
- Pod/container restart count greater than `3` in 10 minutes

Tune these after observing real traffic.

### Application Rollback

For containerized deployment:

1. Stop rollout.
2. Redeploy the previous known-good image tag.
3. Confirm readiness and health checks.
4. Monitor the same dashboards used during rollout.
5. Create an incident note with the bad version, symptoms, and fix plan.

### Database Rollback

Database rollback needs more care than application rollback.

Prefer forward-compatible migrations:

- Add new columns before using them.
- Keep old columns until all app versions no longer need them.
- Avoid destructive migrations in the same release as application changes.
- Use nullable columns or safe defaults when adding fields.
- Deploy schema changes before application changes when possible.

If a schema migration fails:

1. Stop the rollout.
2. Disable traffic to the new application version.
3. Restore from backup only when data is corrupted or unrecoverable.
4. Prefer a forward-fix migration when data is intact.
5. Validate data consistency before re-enabling traffic.
6. For Database system we can also keep a in sync replica as back with cutover till prod migration in standby so that we can roll back to a previous setup effectivly incase of major changes.

## Observability Plan

### Metrics

Expose metrics through Spring Boot Actuator and scrape them with Prometheus or another monitoring platform.

Key service metrics:

- Request rate by endpoint
- HTTP status code counts
- 4xx and 5xx error rates
- P50, P95, and P99 latency
- Active requests
- JVM heap and non-heap memory
- Thread count
- CPU usage
- Container restarts
- Database connection pool usage

Key business metrics:

- Number of pending shift swap requests
- Number of approved shift swap requests
- Number of rejected shift swap requests
- Shift swap request creation failures
- Shift swap resolution failures
- Time from request creation to resolution

Useful endpoint-level monitoring:

- `POST /shiftsSwap/swap`
- `POST /shiftsSwap/resolve`
- `GET /shiftsSwap/open`
- `GET /shiftsSwap/employee/{employeeId}`
- `GET /shift/employee/{employeeId}`

### Logs

Use structured JSON logs in production. Include:

- Timestamp
- Log level
- Request method and path
- HTTP status
- Latency
- Request ID or correlation ID
- Employee ID when safe and allowed
- Shift change request ID
- Exception type and message

Do not log sensitive data such as passwords, database credentials, or full secrets.

Recommended log events:

- Shift swap request created
- Shift swap request rejected
- Shift swap request approved
- Shift ownership swapped
- Invalid employee ID
- Invalid shift ID
- Requester does not own shift
- Target employee does not own shift


### Alerts

Start with these alerts:

- Service down: health check fails for 2 minutes
- High 5xx rate: greater than `2%` for 5 minutes
- High latency: P95 greater than `1 second` for 10 minutes
- High memory: greater than `85%` for 10 minutes
- High CPU: greater than `80%` for 10 minutes
- Frequent restarts: more than `3` restarts in 10 minutes
- Database unavailable
- Pending shift swap requests unusually high for the business day

## Scaling Strategy

### Scale Up

Scale up when sustained load or latency indicates one instance cannot handle traffic.

Scale up signals:

- CPU above `70%` to `80%` for 10 minutes
- Memory above `80%` for 10 minutes
- P95 latency above target while error rate increases
- Request queueing or active request count keeps rising
- Database connection pool is consistently near maximum
- Shift swap endpoints become slow during peak scheduling periods

Scaling options:

- Increase replica count.
- Increase CPU or memory limits.
- Tune JVM settings with `JAVA_OPTS`.
- Move from H2 to a production database before running multiple replicas.
- Add database indexes for frequent queries.


### Scale Down

Scale down when load is consistently low and service health remains stable.

Scale down signals:

- CPU below `30%` for 30 to 60 minutes
- Memory usage is stable and comfortably below limits
- P95 latency remains within target
- No backlog of pending requests caused by system slowness
- Error rate remains normal

Avoid aggressive scale-down during business hours if shift management has predictable spikes.

### Initial Capacity Recommendation

For a small internal deployment:

- Start with 2 application replicas for availability.
- Use a managed PostgreSQL or MySQL database.
- Set CPU and memory requests based on staging load tests.
- Configure horizontal scaling on CPU and request latency.

Example autoscaling policy:

- Minimum replicas: `2`
- Maximum replicas: `5`
- Target CPU utilization: `70%`
- Add latency-based scaling later if traffic patterns require it.

## Release Checklist

Before production:

- Tests pass.
- Image is built and tagged with Git SHA.
- Configuration is environment-specific.
- Secrets are stored in the deployment platform, not in source control.
- H2 console is disabled.
- Production database migration is reviewed.
- Backup is available.
- Health checks are enabled.
- Dashboard is ready.
- Alerts are enabled.
- Rollback image tag is known.

After production:

- Confirm `/actuator/health` is `UP`.
- Check error rate and latency.
- Test one read endpoint.
- Test shift swap request creation in a safe environment or with test data.
- Verify logs and metrics are arriving.
- Watch dashboards for at least 15 to 30 minutes.

