package com.sentinelx.ingestion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sentinelxOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SENTINEL-X Event Ingestion API")
                        .description("Real-Time Financial Fraud Detection & Cyber Threat Intelligence Streaming Foundation")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("https://sentinelx.io")));
    }
}
