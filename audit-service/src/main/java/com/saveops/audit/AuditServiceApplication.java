package com.saveops.audit;

import com.saveops.common.event.RabbitEventConfig;
import com.saveops.common.web.CommonWebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CommonWebConfig.class, RabbitEventConfig.class})
public class AuditServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuditServiceApplication.class, args);
    }
}

