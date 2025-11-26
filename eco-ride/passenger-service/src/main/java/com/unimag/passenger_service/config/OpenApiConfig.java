package com.unimag.passenger_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI passengerServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Passenger Service API - Eco-Ride LATAM")
                        .version("1.0.0")
                        .description("API para gestión de pasajeros, conductores y calificaciones del sistema de carpooling corporativo Eco-Ride LATAM")
                        .contact(new Contact()
                                .name("Eco-Ride Team")
                                .email("eco-ride@unimag.edu.co"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Servidor de desarrollo - Passenger Service"),
                        new Server()
                                .url("http://localhost:8080/api/v1")
                                .description("Servidor de producción - API Gateway")
                ));
    }
}