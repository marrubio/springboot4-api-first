package es.marugi.spring.api.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test & !security")
public class RabbitMqConfiguration {

    static final String GAME_EVENTS_EXCHANGE = "game.events";
    static final String GAME_EVENTS_QUEUE = "game.events.queue";
    static final String GAME_EVENTS_DLX = "game.events.dlx";
    static final String GAME_EVENTS_DLQ = "game.events.dlq";

    @Bean
    TopicExchange gameEventsExchange() {
        return new TopicExchange(GAME_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    DirectExchange gameEventsDeadLetterExchange() {
        return new DirectExchange(GAME_EVENTS_DLX, true, false);
    }

    @Bean
    Queue gameEventsQueue() {
        return new Queue(GAME_EVENTS_QUEUE, true, false, false, java.util.Map.of(
            "x-dead-letter-exchange", GAME_EVENTS_DLX
        ));
    }

    @Bean
    Queue gameEventsDeadLetterQueue() {
        return new Queue(GAME_EVENTS_DLQ, true);
    }

    @Bean
    Binding gameEventsBinding(Queue gameEventsQueue, TopicExchange gameEventsExchange) {
        return BindingBuilder.bind(gameEventsQueue).to(gameEventsExchange).with("games.#");
    }

    @Bean
    Binding gameEventsDeadLetterBinding(Queue gameEventsDeadLetterQueue, DirectExchange gameEventsDeadLetterExchange) {
        return BindingBuilder.bind(gameEventsDeadLetterQueue).to(gameEventsDeadLetterExchange).with("#");
    }

    @Bean
    MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter(tools.jackson.databind.json.JsonMapper.builder()
            .findAndAddModules()
            .build());
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    @Bean
    BeanPostProcessor rabbitPublisherConfirmConfiguration() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                if (bean instanceof CachingConnectionFactory connectionFactory) {
                    connectionFactory.setPublisherConfirmType(ConfirmType.CORRELATED);
                    connectionFactory.setPublisherReturns(true);
                }
                return bean;
            }
        };
    }
}