package com.saveops.account.config;

import com.saveops.common.event.EventConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountRabbitBindingsConfig {
    @Bean
    public Queue accountRoundUpQueue() {
        return new Queue(EventConstants.ACCOUNT_ROUND_UP_QUEUE, true);
    }

    @Bean
    public Binding accountRoundUpBinding(Queue accountRoundUpQueue, TopicExchange saveOpsExchange) {
        return BindingBuilder.bind(accountRoundUpQueue).to(saveOpsExchange).with(EventConstants.PURCHASE_ROUNDED_UP);
    }
}

