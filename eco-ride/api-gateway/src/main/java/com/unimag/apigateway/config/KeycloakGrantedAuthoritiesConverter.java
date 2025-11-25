package com.unimag.apigateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Extractor de authorities desde JWT de Keycloak.
 * Convierte roles del realm y scopes en GrantedAuthority para Spring Security.
 */

public class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";
    private static final String SCOPE = "scope";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SCOPE_PREFIX = "SCOPE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        // Extraer roles del realm
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
        if (realmAccess != null && realmAccess.containsKey(ROLES)) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get(ROLES);

            List<GrantedAuthority> realmAuthorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()))
                    .collect(Collectors.toList());

            grantedAuthorities.addAll(realmAuthorities);

        }

        // Extraer scopes (separados por espacios)
        String scopes = jwt.getClaim(SCOPE);
        if (scopes != null && !scopes.isEmpty()) {
            List<GrantedAuthority> scopeAuthorities = Arrays.stream(scopes.split(" "))
                    .map(scope -> new SimpleGrantedAuthority(SCOPE_PREFIX + scope))
                    .collect(Collectors.toList());

            grantedAuthorities.addAll(scopeAuthorities);
        }

        return grantedAuthorities;
    }
}
