package com.blogapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI blogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blog Application API")
                        .description("REST API documentation for WeeBlog - Production Environment")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Blog Admin")
                                .email("weeblogs.info@gmail.com")))
                .servers(List.of(
                        new Server().url("https://api.weeblogs.com").description("Production Server (HTTPS)"),
                        new Server().url("http://93.127.194.118:8027").description("VPS Direct Access"),
                        new Server().url("http://localhost:8080").description("Local Development")
                ));
    }
}
