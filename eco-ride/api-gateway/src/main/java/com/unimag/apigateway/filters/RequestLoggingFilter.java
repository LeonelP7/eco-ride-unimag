package com.unimag.apigateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


import java.util.UUID;

@Component
public class RequestLoggingFilter extends AbstractGatewayFilterFactory<RequestLoggingFilter.Config> {

    public RequestLoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config  config) {
        return (exchange, chain) -> {
            long startTime = System.currentTimeMillis();
            String requestId = UUID.randomUUID().toString();

            return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .flatMap(auth -> {
                        String username = auth != null ? auth.getName() : "anonymous";

                        System.out.printf("[%s] %s %s - User: %s%n",
                                requestId,
                                exchange.getRequest().getMethod(),
                                exchange.getRequest().getPath(),
                                username
                        );

                        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                            long duration = System.currentTimeMillis() - startTime;
                            System.out.printf("[%s] Response: %s - Duration: %dms%n",
                                    requestId,
                                    exchange.getResponse().getStatusCode(),
                                    duration
                            );
                        }));
                    })
                    .switchIfEmpty(Mono.defer(() -> chain.filter(exchange))).then();
        };
    }

    public static class Config{
        // Configuración adicional
    }
}
