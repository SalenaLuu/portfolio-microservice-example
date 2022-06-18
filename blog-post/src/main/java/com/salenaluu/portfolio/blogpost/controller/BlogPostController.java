package com.salenaluu.portfolio.blogpost.controller;

import com.salenaluu.portfolio.blogpost.service.BlogPostServiceImpl;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequest;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequestUpdate;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostResponse;
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

    @PostMapping
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BlogPostResponse> createBlogPost(
            @RequestBody BlogPostRequest blogPostRequest,
            @RequestParam String creatorEmail){
       return blogPostServiceImpl.createBlogPost(blogPostRequest,creatorEmail);
    }

    @GetMapping("/find")
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BlogPostResponse> getBlogPostByTitleAndCreatorEmail(
            @RequestParam String title,
            @RequestParam String creatorEmail){
        return blogPostServiceImpl.getBlogPostByTitleAndCreatorEmail(title,creatorEmail);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<BlogPostResponse> getAllBlogPosts(){
        return blogPostServiceImpl.getAllBlogPosts();
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public Flux<BlogPostResponse> getBlogPostByTags(
            @RequestParam String[] tags){
        return blogPostServiceImpl.getAllBlogPostsWithTags(tags);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BlogPostResponse> updateBlogPost(
            @RequestBody BlogPostRequestUpdate blogPostRequest,
            @RequestParam String creatorEmail){
        return blogPostServiceImpl.updateBlogPost(blogPostRequest,creatorEmail);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBlogPostByTitleAndCreatorEmail(
            @RequestParam String title,
            @RequestParam String creatorEmail){
        return blogPostServiceImpl.deleteBlogPostByTitleAndCreatorEmail(title,creatorEmail);
    }
}
