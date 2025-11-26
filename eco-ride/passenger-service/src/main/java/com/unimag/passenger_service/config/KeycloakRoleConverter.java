package com.unimag.passenger_service.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Extraer roles de realm_access
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> realmRoles = (List<String>) realmAccess.get("roles");
            authorities.addAll(
                    realmRoles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList())
            );
        }

        // Extraer roles de resource_access (opcional, si usas roles específicos de cliente)
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            resourceAccess.values().forEach(resource -> {
                if (resource instanceof Map) {
                    Map<String, Object> resourceMap = (Map<String, Object>) resource;
                    if (resourceMap.containsKey("roles")) {
                        List<String> clientRoles = (List<String>) resourceMap.get("roles");
                        authorities.addAll(
                                clientRoles.stream()
                                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                        .collect(Collectors.toList())
                        );
                    }
                }
            });
        }

        return authorities;
    }
}
