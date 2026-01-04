package com.saveops.account.integration;

import com.saveops.account.entity.AccountEntity;
import com.saveops.account.entity.AccountStatus;
import com.saveops.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountPostgresContainerTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("saveops_account_test")
            .withUsername("saveops")
            .withPassword("saveops");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    AccountRepository accountRepository;

    @Test
    void flywaySchemaSupportsPersistingAccount() {
        UUID accountId = UUID.randomUUID();
        accountRepository.save(new AccountEntity(accountId, "user-1", "RUB", new BigDecimal("100.00"), AccountStatus.ACTIVE, Instant.now()));

        assertThat(accountRepository.findById(accountId))
                .isPresent()
                .get()
                .extracting(AccountEntity::getBalance)
                .isEqualTo(new BigDecimal("100.00"));
    }
}

