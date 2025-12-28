package com.saveops.notification.config;

import com.saveops.common.event.EventConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class NotificationRabbitConfig {
    private static final List<String> EVENT_KEYS = List.of(
            EventConstants.ACCOUNT_OPENED,
            EventConstants.MONEY_TRANSFERRED,
            EventConstants.INTEREST_ACCRUED,
            EventConstants.PURCHASE_ROUNDED_UP
    );

    @Bean
    public DirectExchange notificationRetryExchange() {
        return new DirectExchange(EventConstants.RETRY_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange notificationDlxExchange() {
        return new DirectExchange(EventConstants.DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(EventConstants.NOTIFICATION_QUEUE)
                .deadLetterExchange(EventConstants.RETRY_EXCHANGE)
                .build();
    }

    @Bean
    public Queue notificationRetryQueue() {
        return QueueBuilder.durable(EventConstants.NOTIFICATION_RETRY_QUEUE)
                .ttl(5000)
                .deadLetterExchange(EventConstants.EXCHANGE)
                .build();
    }

    @Bean
    public Queue notificationDlq() {
        return QueueBuilder.durable(EventConstants.NOTIFICATION_DLQ).build();
    }

    @Bean
    public Binding notificationDlqBinding(Queue notificationDlq, DirectExchange notificationDlxExchange) {
        return BindingBuilder.bind(notificationDlq).to(notificationDlxExchange).with(EventConstants.NOTIFICATION_FAILED);
    }

    @Bean
    public List<Binding> notificationBindings(Queue notificationQueue, TopicExchange saveOpsExchange) {
        return EVENT_KEYS.stream()
                .map(key -> BindingBuilder.bind(notificationQueue).to(saveOpsExchange).with(key))
                .toList();
    }

    @Bean
    public List<Binding> notificationRetryBindings(Queue notificationRetryQueue, DirectExchange notificationRetryExchange) {
        return EVENT_KEYS.stream()
                .map(key -> BindingBuilder.bind(notificationRetryQueue).to(notificationRetryExchange).with(key))
                .toList();
    }
}

