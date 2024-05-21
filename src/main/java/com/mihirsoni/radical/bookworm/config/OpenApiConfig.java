package com.mihirsoni.radical.bookworm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Value("${bookworm.dev-url}")
  private String devUrl;

  @Value("${bookworm.prod-url}")
  private String prodUrl;

  @Bean
  public OpenAPI myOpenAPI() {
    Server devServer = new Server();
    devServer.setUrl(devUrl);
    devServer.setDescription("Server URL in Development environment");

    Server prodServer = new Server();
    prodServer.setUrl(prodUrl);
    prodServer.setDescription("Server URL in Production environment");

    Contact contact = new Contact();
    contact.setEmail("mihirsoni826@gmail.com");
    contact.setName("Mihir Soni");

    Info info =
        new Info()
            .title("Bookworm Backend API")
            .version("1.0")
            .contact(contact)
            .description("This API acts as a backend for the Bookworm React App.");

    return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
  }
}
