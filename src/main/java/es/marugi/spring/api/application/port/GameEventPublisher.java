package es.marugi.spring.api.application.port;

import es.marugi.spring.api.application.event.GameEvent;

public interface GameEventPublisher {

    void publish(GameEvent event);
}