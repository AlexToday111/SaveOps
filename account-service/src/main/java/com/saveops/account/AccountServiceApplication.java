package com.saveops.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.saveops.common.event.RabbitEventConfig;
import com.saveops.common.web.CommonWebConfig;

@SpringBootApplication
@Import({CommonWebConfig.class, RabbitEventConfig.class})
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}

