package com.unimag.apigateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;


/**
 * Filtro personalizado para validar roles específicos en operaciones de viajes.
 * Implementa las políticas:
 * - ROLE_DRIVER puede publicar viajes (POST /trips)
 * - ROLE_PASSENGER puede reservar (POST /trips/{id}/bookings)
 * - ROLE_ADMIN puede gestionar lo que sea
 */

@Component
public class RoleBasedAccessFilter extends AbstractGatewayFilterFactory<RoleBasedAccessFilter.Config> {


    public RoleBasedAccessFilter() {
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) ->{
            String path =  exchange.getRequest().getPath().toString();
            String method = exchange.getRequest().getMethod().toString();

            return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .flatMap(authentication -> {
                        if(authentication==null || !authentication.isAuthenticated()) {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }

                        // Validar políticas específicas
                        if (path.matches(".*/trips$") && "POST".equals(method)) {
                            // Solo DRIVER o ADMIN pueden publicar viajes
                            if (!hasRole(authentication, "DRIVER") && !hasRole(authentication, "ADMIN")) {
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                return exchange.getResponse().setComplete();
                            }
                        } else if (path.matches(".*/trips/\\d+/bookings$") && "POST".equals(method)) {
                            // Solo PASSENGER o ADMIN pueden reservar
                            if (!hasRole(authentication, "PASSENGER") && !hasRole(authentication, "ADMIN")) {
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                return exchange.getResponse().setComplete();
                            }
                        } else if (path.matches(".*/trips/\\d+$") && ("PUT".equals(method) || "DELETE".equals(method))) {
                            // Solo DRIVER (propietario) o ADMIN pueden modificar/eliminar
                            // Aquí se podría agregar validación adicional de ownership
                            if (!hasRole(authentication, "DRIVER") && !hasRole(authentication, "ADMIN")) {
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                return exchange.getResponse().setComplete();
                            }
                        }
                        return chain.filter(exchange);
                    })
                    .switchIfEmpty(chain.filter(exchange));
        };
    }


    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_" + role)
                );
    }

    public static class Config  {
    }
}
