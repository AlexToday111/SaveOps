package com.saveops.account.integration;

import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class RabbitRedisContainerTest {
    @Container
    static final RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7.4-alpine").withExposedPorts(6379);

    @Test
    void rabbitMqAcceptsPublishedDomainEventMessage() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbit.getHost());
        factory.setPort(rabbit.getAmqpPort());
        factory.setUsername(rabbit.getAdminUsername());
        factory.setPassword(rabbit.getAdminPassword());

        try (var connection = factory.newConnection(); var channel = connection.createChannel()) {
            channel.exchangeDeclare("saveops.events.test", "topic", true);
            channel.queueDeclare("saveops.events.test.queue", true, false, true, null);
            channel.queueBind("saveops.events.test.queue", "saveops.events.test", "account.opened");
            channel.basicPublish("saveops.events.test", "account.opened", null, "ok".getBytes(StandardCharsets.UTF_8));

            var message = channel.basicGet("saveops.events.test.queue", true);

            assertThat(message).isNotNull();
            assertThat(new String(message.getBody(), StandardCharsets.UTF_8)).isEqualTo("ok");
        }
    }

    @Test
    void redisStoresIdempotencyKey() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redis.getHost(), redis.getMappedPort(6379));
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(configuration);
        connectionFactory.afterPropertiesSet();
        try {
            StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
            template.opsForValue().set("account:idempotency:test", "created");

            assertThat(template.opsForValue().get("account:idempotency:test")).isEqualTo("created");
        } finally {
            connectionFactory.destroy();
        }
    }
}
