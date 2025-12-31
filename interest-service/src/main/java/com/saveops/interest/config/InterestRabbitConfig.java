package com.saveops.interest.config;

import com.saveops.common.event.EventConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterestRabbitConfig {
    public static final String ACCOUNT_OPENED_QUEUE = "saveops.interest.account-opened";

    @Bean
    public Queue interestAccountOpenedQueue() {
        return new Queue(ACCOUNT_OPENED_QUEUE, true);
    }

    @Bean
    public Binding interestAccountOpenedBinding(Queue interestAccountOpenedQueue, TopicExchange saveOpsExchange) {
        return BindingBuilder.bind(interestAccountOpenedQueue).to(saveOpsExchange).with(EventConstants.ACCOUNT_OPENED);
    }
}

