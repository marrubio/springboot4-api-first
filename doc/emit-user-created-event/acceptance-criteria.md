# Acceptance Criteria

## Functional Criteria

- [ ] Given a valid new-user request, when the existing user-create flow succeeds, then the user is persisted and exactly one typed `UserCreatedEvent` is enqueued.
- [ ] Given a successful save, when the event is created, then it is built from the saved user and contains the generated persisted user ID.
- [ ] Given a successful user creation, when the event metadata is inspected, then it has a non-null UUID event ID, an occurrence timestamp from the injected `Clock`, and the same type/version conventions as the established game-created event.
- [ ] Given a successful user creation, when the event snapshot is inspected, then it contains the appropriate persisted user fields and never contains the password.
- [ ] Given a duplicate login/email validation failure or persistence failure, when user creation is rejected, then no user-created event is enqueued.
- [ ] Given a user update or delete operation, when it is executed, then this feature produces no user-created event.

## API Criteria

- [ ] The existing user-create endpoint, request schema, response schema, status code, and validation/error behavior are unchanged.
- [ ] No new endpoint, OpenAPI operation, generated API type, request field, or response field is introduced.
- [ ] The existing user response continues to omit the password.
- [ ] Backward compatibility impact is documented: existing user and game API behavior remains unchanged; event emission is an internal durable side effect of successful user creation.

## Architecture Criteria

- [ ] User creation remains orchestrated by the existing application service, with controllers/delegates unchanged and thin.
- [ ] Persistence and event enqueueing occur within the same existing transaction, following the `GameServiceImpl.createGame` ordering and transactional-outbox pattern.
- [ ] The application service depends on application-level outbox abstractions and `Clock`, not on Gruelbox, RabbitMQ, broker configuration, or framework-specific delivery code.
- [ ] The new event, snapshot, ports, adapter, and serializer registration follow the existing package/layer/naming conventions.
- [ ] Existing game event classes, outbox behavior, outbox timing/retry configuration, and broker topology remain unchanged.
- [ ] No consumer, broker provisioning, retry/DLQ policy, or user update/delete event is added.

## Test And Validation Criteria

- [ ] Focused service tests verify event type, version, UUID ID, injected-clock timestamp, persisted user ID, safe snapshot contents, enqueue ordering, and password exclusion.
- [ ] Failure-path tests verify that duplicate validation and persistence failures do not enqueue an event.
- [ ] A focused transactional-outbox test verifies that user persistence and event enqueueing are atomic and use the established outbox writer/serializer path.
- [ ] Existing user API integration tests pass without changed API expectations.
- [ ] Relevant architecture tests pass.
- [ ] The full test profile passes.
- [ ] Documentation is updated only if existing event documentation covers this behavior; no API documentation change is required.