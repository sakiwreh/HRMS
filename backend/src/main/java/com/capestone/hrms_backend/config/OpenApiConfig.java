package com.capestone.hrms_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("HRMS APIs")
                        .description("API Documentation for Human Resource Management System")
                        .version("1.0.0")
                        .contact(new Contact().name("Rehan Sakiwala").email("rehan.sakiwala@roima.com")));
    }
}
