package es.marugi.spring.api.adapter.out.messaging;

import es.marugi.spring.api.application.event.GameEvent;
import es.marugi.spring.api.application.port.GameEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test | security")
public class TestGameEventPublisher implements GameEventPublisher {

    @Override
    public void publish(GameEvent event) {
        // The Transaction Outbox invocation is verified without connecting to a broker in tests.
    }
}