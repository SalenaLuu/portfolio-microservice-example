package com.salenaluu.portfolio.usermanagement.controller;

import com.salenaluu.portfolio.usermanagement.model.MyUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/userdata")
public class UserManagementController {

    @GetMapping
    public Mono<MyUser> userdata(@AuthenticationPrincipal OidcUser oidcUser,
                                @RegisteredOAuth2AuthorizedClient("okta")OAuth2AuthorizedClient client){

        MyUser myUser = new MyUser();

        myUser.setUserName(oidcUser.getFullName());
        myUser.setEmail(oidcUser.getEmail());
        myUser.setIdToken(oidcUser.getIdToken().getTokenValue());
        myUser.setAccessToken(client.getAccessToken().getTokenValue());
        return Mono.just(myUser);
    }

    @GetMapping("/email")
    public Mono<String> currentEmail(@AuthenticationPrincipal OidcUser oidcUser){
        return Mono.just(oidcUser.getEmail());
    }
}
