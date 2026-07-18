package es.marugi.spring.api.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

class RabbitMqConfigurationTest {

    private final RabbitMqConfiguration configuration = new RabbitMqConfiguration();

    @Test
    void configuresRabbitTemplateWithJsonMessageConverter() {
        MessageConverter messageConverter = configuration.rabbitMessageConverter();

        RabbitTemplate rabbitTemplate = configuration.rabbitTemplate(
            mock(ConnectionFactory.class), messageConverter);

        assertThat(rabbitTemplate.getMessageConverter()).isSameAs(messageConverter)
            .isInstanceOf(JacksonJsonMessageConverter.class);
    }
}