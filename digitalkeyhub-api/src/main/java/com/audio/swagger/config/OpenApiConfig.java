package com.audio.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        List<String> orderStatusValues = Arrays.asList(
                "CREATED",
                "PROCESSING",
                "PAID",
                "SHIPPED",
                "DELIVERED",
                "CANCELLED",
                "REFUNDED",
                "FAILED",
                "COMPLETED"
        );

        var orderStatusDescription = """
                Possible order status values:\s
                - CREATED: Order has been created
                - PROCESSING: Order is being processed
                - PAID: Order has been paid
                - SHIPPED: Order has been shipped
                - DELIVERED: Order has been delivered
                - CANCELLED: Order has been cancelled
                - REFUNDED: Order has been refunded
                - FAILED: Order processing failed
                - COMPLETED: Order completed successfully""";

        return new OpenAPI()
                .info(new Info()
                        .title("DigitalKeyHub API")
                        .version("1.0")
                        .description("API documentation for DigitalKeyHub"))
                .components(new Components()

                        .addSecuritySchemes("stripeWebhook",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Stripe-Signature"))

                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))

                        .addSchemas("OrderStatus", new StringSchema()
                                .description(orderStatusDescription)
                                ._enum(orderStatusValues))

                        .addSchemas("OrderItemServiceResponse", new ObjectSchema()
                                .description("Response object representing an order item")
                                .addProperty("id", new StringSchema()
                                        .description("Unique identifier of the order item")
                                        .example("123e4567-e89b-12d3-a456-426614174000"))
                                .addProperty("productId", new StringSchema()
                                        .description("ID of the product")
                                        .example("123e4567-e89b-12d3-a456-426614174000"))
                                .addProperty("quantity", new IntegerSchema()
                                        .description("Quantity ordered")
                                        .example(2))
                                .addProperty("unitPrice", new NumberSchema()
                                        .format("double")
                                        .description("Price per unit at time of order")
                                        .example(19.99))
                                .addProperty("subtotal", new NumberSchema()
                                        .format("double")
                                        .description("Subtotal for this item")
                                        .example(39.98))));
    }
}