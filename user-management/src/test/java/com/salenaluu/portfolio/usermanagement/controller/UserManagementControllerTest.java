package com.salenaluu.portfolio.usermanagement.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOidcLogin;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureWebTestClient
class UserManagementControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    String baseUrl = "/api/v1/userdata";

    @Test
    @DisplayName("should get no Auth returnsRedirectLogin()")
    void should_get_noAuth_returnsRedirectLogin() {
        webTestClient
                .get()
                .uri(baseUrl)
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    @DisplayName("should get with Oidc-Login returns ok()")
    void should_get_withOidcLogin_returnsOK() {
        webTestClient
                .mutateWith(mockOidcLogin().idToken(token -> token.claim("name","Mock User")))
                .get()
                .uri(baseUrl)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.userName").isNotEmpty()
                //.jsonPath("$.email").isNotEmpty()
                .jsonPath("$.idToken").isNotEmpty()
                .jsonPath("$.accessToken").isNotEmpty();
    }
}