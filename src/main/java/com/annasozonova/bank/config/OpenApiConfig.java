package com.annasozonova.bank.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI 3.0 (Swagger UI) metadata and security.
 * <p>
 * Enables auto-generated documentation at <code>/swagger-ui.html</code> or <code>/swagger-ui/index.html</code>.
 * Adds JWT bearer token support globally.
 * </p>
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Bank Card Management API",
                version = "1.0",
                description = "API for managing bank cards, transfers, and user accounts",
                contact = @Contact(name = "Anna Sozonova", email = "sozonovanna@gmail.com"),
                license = @License(name = "MIT")
        ),
        servers = @Server(url = "/"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class OpenApiConfig {
}
