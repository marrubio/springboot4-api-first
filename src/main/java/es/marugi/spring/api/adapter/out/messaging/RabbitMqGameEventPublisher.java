package es.marugi.spring.api.adapter.out.messaging;

import es.marugi.spring.api.application.event.GameEvent;
import es.marugi.spring.api.application.port.GameEventPublisher;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
@Profile("!test & !security")
public class RabbitMqGameEventPublisher implements GameEventPublisher {

    private static final String EXCHANGE = "game.events";

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqGameEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(GameEvent event) {
        try {
            CorrelationData correlationData = new CorrelationData(event.eventId().toString());
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey(event), event, message -> {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setMessageId(event.eventId().toString());
                message.getMessageProperties().setType(event.eventType());
                message.getMessageProperties().setHeader("eventVersion", event.eventVersion());
                return message;
            }, correlationData);
            CorrelationData.Confirm confirm = correlationData.getFuture().get(5, TimeUnit.SECONDS);
            if (!confirm.ack() || correlationData.getReturned() != null) {
                throw new IllegalStateException("RabbitMQ did not accept game event " + event.eventId());
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing game event " + event.eventId(), exception);
        } catch (java.util.concurrent.ExecutionException | java.util.concurrent.TimeoutException exception) {
            throw new IllegalStateException("Unable to confirm game event " + event.eventId(), exception);
        }
    }

    private String routingKey(GameEvent event) {
        return switch (event) {
            case es.marugi.spring.api.application.event.GameCreatedEvent ignored -> "games.created";
            case es.marugi.spring.api.application.event.GameUpdatedEvent ignored -> "games.updated";
            case es.marugi.spring.api.application.event.GameDeletedEvent ignored -> "games.deleted";
        };
    }
}