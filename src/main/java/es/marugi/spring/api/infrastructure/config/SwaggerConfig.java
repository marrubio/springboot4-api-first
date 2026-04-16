package es.marugi.spring.api.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Custom Swagger/OpenAPI configuration for the Game API.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Game API")
                        .version("1.0.0")
                        .description("REST API for game management using API First paradigm")
                        .contact(new Contact()
                                .name("Mario Rubio")
                                .email("marugi@gmail.com")));
    }
}

