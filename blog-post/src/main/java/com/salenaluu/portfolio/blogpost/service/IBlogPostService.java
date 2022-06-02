package com.salenaluu.portfolio.blogpost.service;

import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequest;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequestUpdate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBlogPostService {
    /* ===== CREATE =====*/
    Mono<BlogPostRequest> createBlogPost(BlogPostRequest blogPostRequest);
    /* ===== READ ===== */
    Mono<BlogPostRequest> getBlogPostByTitleAndCreatorEmail(String title, String email);
    Flux<BlogPostRequest> getAllBlogPosts();
    Flux<BlogPostRequest> getAllBlogPostsWithTags(String[] tags);
    /* ===== UPDATE ===== */
    Mono<BlogPostRequest> updateBlogPost(BlogPostRequestUpdate blogPostRequest, String email);
    /* ===== DELETE ===== */
    Mono<Void> deleteBlogPostByTitleAndCreatorEmail(String title, String email);
}