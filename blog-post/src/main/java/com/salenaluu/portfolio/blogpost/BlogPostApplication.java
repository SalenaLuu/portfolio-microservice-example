package com.salenaluu.portfolio.blogpost;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
			title = "blog-post service",
			version = "1.0",
			description = "Application to CRUD BlogPosts"))
public class BlogPostApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogPostApplication.class, args);
	}

}