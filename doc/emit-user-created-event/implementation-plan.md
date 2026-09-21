# Implementation Plan

## Feature Summary

- Request: Emit a typed `UserCreatedEvent` when a new user is created.
- User-visible behavior: The existing user-create use case continues to return its current successful response, while the persisted user creation also schedules a durable outbox event.
- Out of scope: REST or OpenAPI changes, user update/delete events, consumers, broker provisioning, broker-specific delivery changes, and new retry/DLQ behavior.

## Current Behavior

- Relevant existing flow: The existing create-user application service validates uniqueness, maps the create request, persists the user, and maps the saved user to the existing response. The analogous `GameServiceImpl.createGame` flow persists first and then enqueues a typed created event through a transactional outbox writer in the same transaction.
- Relevant files or packages: `application.service.UserServiceImpl`, the user repository and mapper, the existing `application.event` game event types and snapshots, the application outbox ports, `adapter.out.persistence.TransactionOutboxGameEventWriter`, and `EventDispatchConfiguration`.
- Constraints from architecture or tests: Preserve the current hexagonal/onion boundaries and existing game event behavior. The application service must use application ports rather than Gruelbox, RabbitMQ, or other broker infrastructure. The event must be constructed from the saved user, use an injected `Clock`, and preserve the existing user API contract.

## Proposed Design

- API contract changes: None. Do not change the OpenAPI document, generated API sources, request/response DTOs, endpoint status codes, or response fields.
- REST layer changes: None. Keep the existing controller/delegate and REST mapping unchanged.
- Application layer changes: Mirror the established game event contract with a typed user-created event and immutable user snapshot. Add the corresponding application-level outbox writer/publisher ports if required by the existing event pattern. Inject the outbox writer and `Clock` into `UserServiceImpl`; in the existing transactional `createUser` method, save the user first, create one event with a generated UUID, `Instant.now(clock)`, the established event type/version metadata, and a snapshot of the persisted user, then enqueue it through the writer. Do not enqueue events from update or failure paths.
- Domain layer changes: None to the `User` domain model or its persistence contract.
- Persistence layer changes: Add the user event/outbox adapter following `TransactionOutboxGameEventWriter`, so enqueueing schedules the typed event through the existing transactional outbox mechanism. Register the user event types (and snapshot type if required by the serializer) with the existing outbox invocation serializer. Do not change outbox timing, recovery, retry, or blocking configuration.
- Security or configuration changes: No security or broker topology changes. Do not add consumers, exchanges, queues, routing keys, provisioning, or broker-specific user-event behavior.
- Documentation changes: Document the durable user-created event only if the repository's existing event documentation requires it; explicitly state that the event snapshot excludes the password. No API documentation update is needed.

## Implementation Steps

1. Inspect and mirror the existing game created-event, snapshot, outbox port, transaction-outbox writer, serializer registration, and test conventions for users.
2. Add the typed user-created event contract and safe persisted-user snapshot, excluding the password.
3. Add the user outbox application ports and transactional outbox adapter/configuration registration using the same pattern as game events.
4. Inject the user outbox writer and `Clock` into `UserServiceImpl`; after a successful save in `createUser`, enqueue exactly one event built from the saved entity.
5. Add focused tests for event metadata, fixed-clock timestamps, persisted IDs and snapshot contents, ordering, duplicate/failure paths, and transactional outbox behavior without changing API assertions.
6. Run focused user-service/outbox tests, existing user API tests, the architecture tests, and the full test profile.

## Test Plan

- Unit tests: Verify successful creation enqueues exactly one typed event after persistence; the event has a UUID ID, event type/version matching the established contract, the injected-clock timestamp, the generated user ID, and a snapshot of the persisted safe fields without a password. Verify duplicate validation and persistence failures enqueue nothing.
- Controller or integration tests: Keep the existing user-create API behavior unchanged (`201` and the current response schema); no new endpoint or API contract test is required.
- Repository or persistence tests: Verify the transactional outbox adapter schedules the typed user event through the established outbox port/serializer conventions and does not introduce a separate broker path.
- Architecture tests: Run the existing onion/layer, naming, controller, and DAO architecture tests.
- Manual checks: Confirm no OpenAPI/generated source, user update/delete flow, broker configuration, consumer, retry/DLQ policy, or game event implementation was changed.

## Risks And Assumptions

- Risks: The existing outbox serializer may require every new event and nested snapshot type to be explicitly allow-listed; omitting one could fail deferred dispatch at runtime. Outbox dispatch is asynchronous/at-least-once according to the established implementation, so tests must assert the stable transaction boundary rather than timing-sensitive broker effects.
- Assumptions: The user event contract and operational semantics should be a user-specific parallel of the existing game event/outbox implementation, including its event type/version conventions and deferred invocation behavior. The event snapshot contains only the persisted user identity/profile fields represented by the established safe user contract and never the password.
- Open questions: Approval is required for this plan and its acceptance criteria before any application code is changed.