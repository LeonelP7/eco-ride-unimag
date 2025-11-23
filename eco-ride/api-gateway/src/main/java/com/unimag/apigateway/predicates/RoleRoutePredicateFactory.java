package com.unimag.apigateway.predicates;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Predicado personalizado para verificar roles de usuario.
 * Uso: - Role=DRIVER,ADMIN
 */

@Component
public class RoleRoutePredicateFactory extends AbstractRoutePredicateFactory<RoleRoutePredicateFactory.Config>{

    public RoleRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return (GatewayPredicate) exchange -> Boolean.TRUE.equals(ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(auth -> hasAnyRole(auth, config.getRoles()))
                .defaultIfEmpty(false)
                .block());
    }

    private boolean hasAnyRole(Authentication auth, List<String> roles) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return roles.stream().anyMatch(role ->
                auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role))
        );
    }

    public static class Config {
        private List<String> roles;

        public List<String> getRoles() {
            return roles;
        }
        public void setRoles(String rolesString) {
            this.roles = Arrays.asList(rolesString.split(","));
        }
    }
}
