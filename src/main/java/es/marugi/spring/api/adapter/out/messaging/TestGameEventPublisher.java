package es.marugi.spring.api.adapter.out.messaging;

import es.marugi.spring.api.application.event.GameEvent;
import es.marugi.spring.api.application.port.GameEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test | security")
public class TestGameEventPublisher implements GameEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(TestGameEventPublisher.class);

    @Override
    public void publish(GameEvent event) {
        logger.warn("Event Message with id {} NOT sent by testGameEventPublisher [TEST]", event.eventId());
    }
}