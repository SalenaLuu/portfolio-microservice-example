package com.salenaluu.portfolio.blogpost.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class BlogPostSecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http){

        http.csrf().disable()
                .authorizeExchange()
                .anyExchange()
                .authenticated()
                .and().oauth2Login()
                .and().oauth2ResourceServer().jwt();
        return http.build();
        /*http.csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.GET,"/","/login/oauth2/code/okta","/api/v1/blogpost","/api/v1/blogpost/filter")
                    .permitAll()
                .pathMatchers(HttpMethod.GET,"/api/v1/blogpost/{title}")
                    .hasRole("explorer")
                .pathMatchers(HttpMethod.POST,"/api/v1/blogpost")
                    .permitAll()
                .pathMatchers(HttpMethod.PUT,"/api/v1/blogpost/update")
                    .hasRole("explorer")
                .pathMatchers(HttpMethod.DELETE,"/api/v1/blogpost")
                    .hasRole("explorer")
                .anyExchange()
                .authenticated()
                .and().oauth2Login()
                .and().oauth2ResourceServer().jwt();

        return http.build();*/
    }
}
