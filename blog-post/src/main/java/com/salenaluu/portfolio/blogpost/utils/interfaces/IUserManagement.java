package com.salenaluu.portfolio.blogpost.utils.interfaces;

import org.springframework.web.bind.annotation.GetMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient("user-management")
public interface IUserManagement {
    @GetMapping("/api/v1/userdata/email")
    Mono<String> currentEmail();
}
