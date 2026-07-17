package es.marugi.spring.api.infrastructure.config;

import com.gruelbox.transactionoutbox.TransactionOutbox;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TransactionOutboxRecovery {

    private final TransactionOutbox transactionOutbox;

    public TransactionOutboxRecovery(TransactionOutbox transactionOutbox) {
        this.transactionOutbox = transactionOutbox;
    }

    @Scheduled(fixedDelayString = "${app.events.recovery-delay:PT60S}")
    public void recoverPendingPublications() {
        while (transactionOutbox.flush()) {
            // Flush all recovery work currently due; normal delivery is submitted after commit.
        }
    }
}