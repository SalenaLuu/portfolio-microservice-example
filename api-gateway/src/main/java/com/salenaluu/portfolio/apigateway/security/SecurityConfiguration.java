package com.salenaluu.portfolio.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf().disable()
                .authorizeExchange()
                .pathMatchers("/","/v3/api-docs/**", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html")
                    .permitAll()
                .anyExchange()
                .authenticated()
                .and().oauth2Login()
                .and().oauth2ResourceServer().jwt();
        return http.build();
    }
}
