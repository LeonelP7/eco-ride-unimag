package com.unimag.apigateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

//Filtro de validacion de scope

@Component
public class ScopeValidationFilter extends AbstractGatewayFilterFactory<ScopeValidationFilter.Config> {


    public ScopeValidationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return ReactiveSecurityContextHolder.getContext().
                    map(SecurityContext::getAuthentication)
                    .flatMap(authentication -> {

                        if(config.getRequiredScope() != null && !config.getRequiredScope().isEmpty()){
                            boolean hasScope = authentication.getAuthorities().stream().anyMatch(
                                    authority ->
                                            authority.getAuthority().equals("SCOPE_" + config.getRequiredScope())
                            );

                            if(!hasScope){
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                return exchange.getResponse().setComplete();
                            }
                        }

                        return chain.filter(exchange);
                    })
                    .switchIfEmpty(chain.filter(exchange));

        };
    }

    public static class Config {
        private String requiredScope;

        public String getRequiredScope() {
            return requiredScope;
        }

        public void setRequiredScope(String requiredScope) {
            this.requiredScope = requiredScope;
        }
    }
}
