package com.unimag.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

@Configuration
@EnableWebFluxSecurity
@Profile("!test")
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {

        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges  -> exchanges
                        // Public endpoints
                        .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                        .pathMatchers("/fallback/**").permitAll()
                        .pathMatchers("/login**", "/error", "/webjars/**").permitAll()

                        // Trips endpoints - scope and role-based
                        .pathMatchers("/api/trips").hasAuthority("SCOPE_trips:read")
                        .pathMatchers("/api/trips/**").hasAnyAuthority("SCOPE_trips:read", "SCOPE_trips:write")

                        // Payments endpoints - scope-based
                        .pathMatchers("/api/payments/charge").hasAuthority("SCOPE_payments:charge")
                        .pathMatchers("/api/payments/**").hasAnyAuthority("SCOPE_payments:charge", "ROLE_ADMIN")

                        // Notifications endpoints - scope-based
                        .pathMatchers("/api/notifications/send").hasAuthority("SCOPE_notifications:send")
                        .pathMatchers("/api/notifications/**").hasAnyAuthority("SCOPE_notifications:send", "ROLE_ADMIN")

                        // Users endpoints - authenticated users
                        .pathMatchers("/api/users/profile").authenticated()
                        .pathMatchers("/api/users/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            System.out.println("Login exitoso: " + authentication.getName());
                            return webFilterExchange.getExchange().getResponse()
                                    .setComplete();
                        })
                ).oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                )
                .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                );


        return http.build();

    }


    @Bean
    public ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedServerLogoutSuccessHandler successHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("{baseUrl}");
        return successHandler;
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                new KeycloakGrantedAuthoritiesConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
