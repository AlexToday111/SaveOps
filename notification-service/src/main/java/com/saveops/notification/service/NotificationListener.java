package com.saveops.notification.service;

import com.saveops.common.event.DomainEvent;
import com.saveops.common.event.EventConstants;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    private final Counter sentCounter;
    private final Counter failureCounter;

    public NotificationListener(MeterRegistry meterRegistry) {
        this.sentCounter = Counter.builder("saveops_notifications_sent_total").register(meterRegistry);
        this.failureCounter = Counter.builder("saveops_notification_failures_total").register(meterRegistry);
    }

    @RabbitListener(queues = EventConstants.NOTIFICATION_QUEUE)
    public void onEvent(DomainEvent event) {
        try {
            if (Boolean.TRUE.equals(event.payload().get("forceNotificationFailure"))) {
                throw new IllegalStateException("Forced notification failure");
            }
            log.info("Mock notification sent for eventType={} aggregateId={} eventId={}",
                    event.eventType(), event.aggregateId(), event.eventId());
            sentCounter.increment();
        } catch (RuntimeException ex) {
            failureCounter.increment();
            log.warn("Notification failed for eventId={} reason={}", event.eventId(), ex.getMessage());
            throw ex;
        }
    }
}

