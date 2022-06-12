package com.salenaluu.portfolio.blogpost.controller;

import com.salenaluu.portfolio.blogpost.service.BlogPostServiceImpl;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequest;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequestUpdate;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Tag(name = "Blog-Post-Controller", description = "Controller to CRUD Blog-Posts")
@RestController
@RequestMapping("/api/v1/blogpost")
@RequiredArgsConstructor
public class BlogPostController {

    private final BlogPostServiceImpl blogPostServiceImpl;

    @Operation(summary = "create a blog post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Blog-Post created",
            content = {@Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BlogPostResponse.class))}),
            @ApiResponse(responseCode = "400", description = "BLOG_POST_ALREADY_EXISTS",
            content = @Content),
            @ApiResponse(responseCode = "400", description = "REQUESTED_MODEL_INVALID",
            content = @Content),
            @ApiResponse(responseCode = "400", description = "REQUEST_NOT_EXCEPTED",
            content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BlogPostResponse> createBlogPost(
            @Parameter(description = "Model to create Blog-Post")
            @RequestBody BlogPostRequest blogPostRequest,
            @Parameter(description = "Creator-Email of blog-post")
            @RequestParam String creatorEmail){
       return blogPostServiceImpl.createBlogPost(blogPostRequest,creatorEmail);
    }

    @Operation(summary = "get Blog-Post by title and creator-email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog-Post founded",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BlogPostResponse.class))}),
            @ApiResponse(responseCode = "400", description = "REQUEST_NOT_EXCEPTED",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "BLOG_POST_NOT_FOUND",
                    content = @Content)
    })
    @GetMapping("/find")
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BlogPostResponse> getBlogPostByTitleAndCreatorEmail(
            @Parameter(description = "Title of blog-post")
            @RequestParam String title,
            @Parameter(description = "Creator-Email of blog-post")
            @RequestParam String creatorEmail){
        return blogPostServiceImpl.getBlogPostByTitleAndCreatorEmail(title,creatorEmail);
    }

    @Operation(summary = "get all Blog-Posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog-Posts founded",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BlogPostResponse.class))}),
            @ApiResponse(responseCode = "204", description = "NO_CONTENT_IN_DB",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<BlogPostResponse> getAllBlogPosts(){
        return blogPostServiceImpl.getAllBlogPosts();
    }

    @Operation(summary = "Get Blog-Post by tags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog-Posts founded",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BlogPostResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "BLOG_POST_WITH_THESE_TAGS_NOT_FOUND",
                    content = @Content)
    })
    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public Flux<BlogPostResponse> getBlogPostByTags(
            @Parameter(description = "Search for BlogPosts with this Tags")
            @RequestParam String[] tags){
        return blogPostServiceImpl.getAllBlogPostsWithTags(tags);
    }

    @Operation(summary = "Update a Blog-Post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog-Posts updated",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BlogPostResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "BLOG_POST_NOT_FOUND",
                    content = @Content)
    })
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BlogPostResponse> updateBlogPost(
            @Parameter(description = "Blog-Post Model to update Blog-Posts")
            @RequestBody BlogPostRequestUpdate blogPostRequest,
            @Parameter(description = "Creator-Email of blog-post")
            @RequestParam String creatorEmail){
        return blogPostServiceImpl.updateBlogPost(blogPostRequest,creatorEmail);
    }

    @Operation(summary = "Delete a Blog-Post by title and creator-email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog-Posts deleted",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BlogPostResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "BLOG_POST_NOT_FOUND",
                    content = @Content)
    })
    @DeleteMapping
    @PreAuthorize("hasAuthority('portfolio_explorer')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBlogPostByTitleAndCreatorEmail(
            @Parameter(description = "Title of blog-post")
            @RequestParam String title,
            @Parameter(description = "Creator-Email of blog-post")
            @RequestParam String creatorEmail){
        return blogPostServiceImpl.deleteBlogPostByTitleAndCreatorEmail(title,creatorEmail);
    }
}
