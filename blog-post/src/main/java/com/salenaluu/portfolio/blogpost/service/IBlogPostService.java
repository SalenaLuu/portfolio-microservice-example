package com.salenaluu.portfolio.blogpost.service;

import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequest;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequestUpdate;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBlogPostService {
    /* ===== CREATE =====*/
    Mono<BlogPostResponse> createBlogPost(BlogPostRequest blogPostRequest, String email);
    /* ===== READ ===== */
    Mono<BlogPostResponse> getBlogPostByTitleAndCreatorEmail(String title, String email);
    Flux<BlogPostResponse> getAllBlogPosts();
    Flux<BlogPostResponse> getAllBlogPostsWithTags(String[] tags);
    /* ===== UPDATE ===== */
    Mono<BlogPostResponse> updateBlogPost(BlogPostRequestUpdate blogPostRequest, String email);
    /* ===== DELETE ===== */
    Mono<Void> deleteBlogPostByTitleAndCreatorEmail(String title, String email);
}