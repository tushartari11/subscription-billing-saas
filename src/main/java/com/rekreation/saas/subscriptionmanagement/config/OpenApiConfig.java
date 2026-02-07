package com.rekreation.saas.subscriptionmanagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

  @Value("${spring.application.name:Subscription Billing SaaS}")
  private String applicationName;

  @Bean
  public OpenAPI customOpenAPI() {
    final String securitySchemeName = "bearerAuth";

    return new OpenAPI()
      .info(new Info()
        .title(applicationName + " API")
        .version("1.0.0")
        .description("REST API for Subscription Billing SaaS platform. "
          + "Manage subscribers, subscriptions, billing, and payments.")
        .contact(new Contact()
          .name("Support")
          .email("support@rekreation.in"))
        .license(new License()
          .name("Proprietary")
          .url("https://rekreation.in")))
      .servers(List.of(
        new Server().url("http://localhost:8080").description("Development server"),
        new Server().url("https://api.rekreation.in").description("Production server")))
      .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
      .components(new Components()
        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
          .name(securitySchemeName)
          .type(SecurityScheme.Type.HTTP)
          .scheme("bearer")
          .bearerFormat("JWT")
          .description("Enter JWT token")));
  }
}
