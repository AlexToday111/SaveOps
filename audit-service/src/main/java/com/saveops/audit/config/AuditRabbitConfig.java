package com.saveops.audit.config;

import com.saveops.common.event.EventConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditRabbitConfig {
    @Bean
    public Queue auditQueue() {
        return new Queue(EventConstants.AUDIT_QUEUE, true);
    }

    @Bean
    public Binding auditAllEventsBinding(Queue auditQueue, TopicExchange saveOpsExchange) {
        return BindingBuilder.bind(auditQueue).to(saveOpsExchange).with("#");
    }
}

