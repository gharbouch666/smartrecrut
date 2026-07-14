package com.smartrecrute.smartrecrute.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "SmartRecrute API",
        version = "1.0",
        description = "Smart Recruitment Platform API",
        contact = @Contact(name = "SmartRecrute Team", email = "contact@smartrecrute.com")
    )
)
public class SwaggerConfig {
}

