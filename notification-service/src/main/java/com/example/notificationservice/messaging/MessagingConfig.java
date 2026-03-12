package com.example.notificationservice.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class MessagingConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.messaging.rabbitmq", name = "enabled", havingValue = "true")
    TopicExchange notificationExchange(MessagingProperties properties) {
        return new TopicExchange(properties.rabbitmq().exchange(), true, false);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.messaging.rabbitmq", name = "enabled", havingValue = "true")
    Queue notificationQueue(MessagingProperties properties) {
        return new Queue(properties.rabbitmq().queue(), true);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.messaging.rabbitmq", name = "enabled", havingValue = "true")
    Binding notificationBinding(
            Queue notificationQueue,
            TopicExchange notificationExchange,
            MessagingProperties properties) {
        return BindingBuilder.bind(notificationQueue)
                .to(notificationExchange)
                .with(properties.rabbitmq().routingKey());
    }
}
