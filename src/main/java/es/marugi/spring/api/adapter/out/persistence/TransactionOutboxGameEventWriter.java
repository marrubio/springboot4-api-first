package es.marugi.spring.api.adapter.out.persistence;

import com.gruelbox.transactionoutbox.TransactionOutbox;
import es.marugi.spring.api.application.event.GameEvent;
import es.marugi.spring.api.application.port.GameEventOutboxWriter;
import es.marugi.spring.api.application.port.GameEventPublisher;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionOutboxGameEventWriter implements GameEventOutboxWriter {

    private final TransactionOutbox transactionOutbox;

    public TransactionOutboxGameEventWriter(TransactionOutbox transactionOutbox) {
        this.transactionOutbox = transactionOutbox;
    }

    @Override
    public void enqueue(GameEvent event) {
        transactionOutbox.schedule(GameEventPublisher.class).publish(event);
    }
}