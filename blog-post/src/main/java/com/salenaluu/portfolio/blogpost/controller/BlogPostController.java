package com.salenaluu.portfolio.blogpost.controller;

import com.salenaluu.portfolio.blogpost.service.BlogPostServiceImpl;
import com.salenaluu.portfolio.blogpost.utils.interfaces.IUserManagement;
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
    private final IUserManagement iUserManagement;

    @PostMapping
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BlogPostResponse> createBlogPost(@RequestBody BlogPostRequest blogPostRequest){
       return iUserManagement
               .currentEmail()
               .flatMap(req -> blogPostServiceImpl.createBlogPost(blogPostRequest,req));
    }

    @GetMapping("/{title}")
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BlogPostResponse> getBlogPostByTitleAndCreatorEmail(@PathVariable String title){
        return iUserManagement
                .currentEmail()
                .flatMap(req -> blogPostServiceImpl.getBlogPostByTitleAndCreatorEmail(title,req));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<BlogPostResponse> getAllBlogPosts(){
        return blogPostServiceImpl.getAllBlogPosts();
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public Flux<BlogPostResponse> getBlogPostByTags(@RequestParam String[] tags){
        return blogPostServiceImpl.getAllBlogPostsWithTags(tags);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BlogPostResponse> updateBlogPost(@RequestBody BlogPostRequestUpdate blogPostRequest){
        return iUserManagement
                .currentEmail()
                .flatMap(req -> blogPostServiceImpl.updateBlogPost(blogPostRequest,req));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBlogPostByTitleAndCreatorEmail(@RequestParam String title){
        return iUserManagement
                .currentEmail()
                .flatMap(req -> blogPostServiceImpl.deleteBlogPostByTitleAndCreatorEmail(title,req));
    }
}
