package net.pet.project.simplerest.configuration.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

/**
 * Configuration for swagger (based on openapi 3.0)
 * @author Vlad Nosov
 */
@Slf4j
@Configuration
public class SwaggerConfig {

    private static final String LOG_TAG = "[SWAGGER_CONF] ::";

    private static final String ADMIN_PATH = "/api/v1/admin/**";

    private final Environment environment;

    @Autowired
    public SwaggerConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public GroupedOpenApi adminOpenApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch(ADMIN_PATH)
                .build();
    }

    @Bean
    public GroupedOpenApi commonOpenApi() {
        return GroupedOpenApi.builder()
                .group("common")
                .pathsToExclude(ADMIN_PATH)
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("{} init swagger documentation", LOG_TAG);
        OpenAPI openAPI = new OpenAPI()
                .info(buildInfo())
                .components(new Components()
                        .addSecuritySchemes("Basic", buildBaseAuthScheme("Basic auth"))
                );
        buildServers(openAPI);
        return openAPI;
    }

    private Info buildInfo() {
        return new Info()
                .title("Simple rest API")
                .version(environment.getRequiredProperty("swagger.application.version"))
                .description(environment.getRequiredProperty("swagger.application.description"))
                .contact(new Contact().email(environment.getProperty("swagger.contact.email"))
                        .name(environment.getRequiredProperty("swagger.contact.name"))
                )
                .termsOfService("http://swagger.io/terms/")
                .license(new License().name("Apache 2.0").url("http://springdoc.org"));
    }

    private SecurityScheme buildBaseAuthScheme(final String description) {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic")
                .description(description)
                .extensions(Map.of("x-role", "Admins_role"));
    }

    private void buildServers(OpenAPI openAPI) {
        final String url = environment.getProperty("swagger.server.url");
        final String description = environment.getProperty("swagger.server.description");
        if (StringUtils.isNotBlank(url) && StringUtils.isNotBlank(url)) {
            openAPI.servers(List.of(new Server().url(url).description(description)));
        }
    }
}