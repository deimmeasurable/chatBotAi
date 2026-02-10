package org.example.aichatbot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI creseadaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ChatBot AI ")
                        .description("AI-powered logistics, shipping, tariff & customs assistant")
                        .version("1.0.0"));
    }
}

