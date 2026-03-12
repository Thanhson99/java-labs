package com.example.demo.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Declares broker-side topology for local learning runs.
 */
@Configuration
public class MessagingTopologyConfig {

    @Bean
    MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.messaging.kafka", name = "enabled", havingValue = "true")
    KafkaAdmin.NewTopics registrationTopics(RegistrationMessagingProperties properties) {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(properties.kafka().topic())
                        .partitions(1)
                        .replicas(1)
                        .build()
        );
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.messaging.rabbitmq", name = "enabled", havingValue = "true")
    TopicExchange registrationExchange(RegistrationMessagingProperties properties) {
        return new TopicExchange(properties.rabbitmq().exchange(), true, false);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.messaging.rabbitmq", name = "enabled", havingValue = "true")
    Queue registrationQueue(RegistrationMessagingProperties properties) {
        return new Queue(properties.rabbitmq().queue(), true);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.messaging.rabbitmq", name = "enabled", havingValue = "true")
    Binding registrationBinding(
            Queue registrationQueue,
            TopicExchange registrationExchange,
            RegistrationMessagingProperties properties) {
        return BindingBuilder.bind(registrationQueue)
                .to(registrationExchange)
                .with(properties.rabbitmq().routingKey());
    }
}
