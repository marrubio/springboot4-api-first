package es.marugi.spring.api.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gruelbox.transactionoutbox.DefaultPersistor;
import com.gruelbox.transactionoutbox.DefaultInvocationSerializer;
import com.gruelbox.transactionoutbox.Dialect;
import com.gruelbox.transactionoutbox.TransactionOutbox;
import com.gruelbox.transactionoutbox.spring.SpringInstantiator;
import com.gruelbox.transactionoutbox.spring.SpringTransactionManager;
import es.marugi.spring.api.application.event.GameCreatedEvent;
import es.marugi.spring.api.application.event.GameDeletedEvent;
import es.marugi.spring.api.application.event.GameEvent;
import es.marugi.spring.api.application.event.GameUpdatedEvent;
import java.time.Clock;
import java.time.Duration;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableScheduling
public class EventDispatchConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    TransactionOutbox transactionOutbox(PlatformTransactionManager transactionManager, DataSource dataSource,
                                        ApplicationContext applicationContext,
                                        @Value("${spring.datasource.url}") String databaseUrl) {
        return TransactionOutbox.builder()
            .transactionManager(new SpringTransactionManager(transactionManager, dataSource))
            .instantiator(new SpringInstantiator(applicationContext))
            .persistor(DefaultPersistor.builder()
                .dialect(databaseUrl.startsWith("jdbc:h2:") ? Dialect.H2 : Dialect.POSTGRESQL_9)
                .serializer(DefaultInvocationSerializer.builder().serializableTypes(java.util.Set.of(
                    GameEvent.class, GameCreatedEvent.class, GameUpdatedEvent.class, GameDeletedEvent.class
                )).build())
                .build())
            .attemptFrequency(Duration.ofSeconds(60))
            .blockAfterAttempts(3)
            .build();
    }
}