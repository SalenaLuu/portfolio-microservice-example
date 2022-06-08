package com.salenaluu.portfolio.blogpost.controller;

import com.salenaluu.portfolio.blogpost.service.BlogPostServiceImpl;
import com.salenaluu.portfolio.blogpost.utils.interfaces.IUserManagement;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequest;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequestUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/blogpost")
@RequiredArgsConstructor
public class BlogPostController {

    private final BlogPostServiceImpl blogPostServiceImpl;
    private final IUserManagement iUserManagement;

    @PostMapping
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BlogPostRequest> createBlogPost(@RequestBody BlogPostRequest blogPostRequest){
        log.info("Test Info from: " + iUserManagement.currentEmail().subscribe());
        return blogPostServiceImpl.createBlogPost(blogPostRequest);
    }

    @GetMapping("/{title}")
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BlogPostRequest> getBlogPostByTitleAndCreatorEmail(@PathVariable String title){
        String email = "test@example.com"; // TODO: Replace with Oidc
        return blogPostServiceImpl.getBlogPostByTitleAndCreatorEmail(title,email);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<BlogPostRequest> getAllBlogPosts(){
        return blogPostServiceImpl.getAllBlogPosts();
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public Flux<BlogPostRequest> getBlogPostByTags(@RequestParam String[] tags){
        return blogPostServiceImpl.getAllBlogPostsWithTags(tags);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BlogPostRequest> updateBlogPost(@RequestBody BlogPostRequestUpdate blogPostRequest){
        String email = "test@example.com"; // TODO: Replace with Oidc
        return blogPostServiceImpl.updateBlogPost(blogPostRequest,email);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBlogPostByTitleAndCreatorEmail(@RequestParam String title){
        String email = "test@example.com"; // TODO: Replace with Oidc
        return blogPostServiceImpl.deleteBlogPostByTitleAndCreatorEmail(title,email);
    }
}
