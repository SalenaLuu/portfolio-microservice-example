package com.salenaluu.portfolio.blogpost.service;

import com.salenaluu.portfolio.blogpost.model.BlogPost;
import com.salenaluu.portfolio.blogpost.repository.IBlogPostRepository;
import com.salenaluu.portfolio.blogpost.utils.enums.Tags;
import com.salenaluu.portfolio.blogpost.utils.exceptions.BadRequestException;
import com.salenaluu.portfolio.blogpost.utils.exceptions.NoContentException;
import com.salenaluu.portfolio.blogpost.utils.exceptions.NotFoundException;
import com.salenaluu.portfolio.blogpost.utils.interfaces.IDateTimeCreator;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequest;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequestUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Collectors;

import static com.salenaluu.portfolio.blogpost.utils.enums.ExceptionResponse.*;
import static java.lang.String.valueOf;
import static java.util.Arrays.stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements IBlogPostService {

    private final IBlogPostRepository blogPostRepository;

    @Override
    public Mono<BlogPostRequest> createBlogPost(BlogPostRequest blogPostRequest) {
        return blogPostRepository
                .existsBlogPostByTitleAndCreatorEmail(
                        blogPostRequest.title(),
                        blogPostRequest.creatorEmail())
                .flatMap(exists -> {
                    if(exists){
                        return Mono.error(
                                new BadRequestException(valueOf(BLOG_POST_ALREADY_EXISTS)));
                    }else {
                        return blogPostRepository
                                .save(new BlogPost(
                                        UUID.randomUUID(),
                                        blogPostRequest.title(),
                                        blogPostRequest.content(),
                                        IDateTimeCreator.createDateTime(),
                                        blogPostRequest.creatorEmail(),
                                        stream(blogPostRequest.tags())
                                                .map(tags -> Tags.valueOf(tags.toUpperCase()))
                                                .collect(Collectors.toSet())))
                                .map(blogPost -> new BlogPostRequest(
                                        blogPost.getTitle(),
                                        blogPost.getContent(),
                                        blogPost.getCreatorEmail(),
                                        blogPost.getTags().stream()
                                                .map(Enum::toString)
                                                .toArray(String[]::new)))
                                .switchIfEmpty(Mono.error(
                                        new BadRequestException(valueOf(REQUESTED_MODEL_INVALID))))
                                .log();
                    }
                }).switchIfEmpty(Mono.error(
                        new BadRequestException(valueOf(REQUEST_NOT_EXCEPTED))));
    }

    @Override
    public Mono<BlogPostRequest> getBlogPostByTitleAndCreatorEmail(String title, String email) {
        return blogPostRepository
                .existsBlogPostByTitleAndCreatorEmail(title,email)
                .flatMap(exists -> {
                    if(!exists){
                        return Mono.error(
                                new NotFoundException(valueOf(BLOG_POST_NOT_FOUND)));
                    }else {
                        return blogPostRepository
                                .findByTitleAndCreatorEmail(title,email)
                                .map(request -> new BlogPostRequest(
                                        request.getTitle(),
                                        request.getContent(),
                                        request.getCreatorEmail(),
                                        request.getTags().stream()
                                                .map(Enum::toString)
                                                .toArray(String[]::new)))
                                .log();
                    }
                }).switchIfEmpty(Mono.error(
                        new BadRequestException(valueOf(REQUEST_NOT_EXCEPTED))));
    }

    @Override
    public Flux<BlogPostRequest> getAllBlogPosts() {
        return blogPostRepository
                .findAll()
                .map(blogPost -> new BlogPostRequest(
                        blogPost.getTitle(),
                        blogPost.getContent(),
                        blogPost.getCreatorEmail(),
                        blogPost.getTags().stream()
                                .map(Enum::toString)
                                .toArray(String[]::new)))
                .switchIfEmpty(Mono.error(
                        new NoContentException(valueOf(NO_CONTENT_IN_DB))));
    }

    @Override
    public Flux<BlogPostRequest> getAllBlogPostsWithTags(String[] tags) {
        return blogPostRepository
                .findAllByTags(stream(tags)
                        .map(mytags ->
                                Tags.valueOf(mytags.toUpperCase()))
                        .collect(Collectors.toSet()))
                .map(response -> new BlogPostRequest(
                        response.getTitle(),
                        response.getContent(),
                        response.getCreatorEmail(),
                        response.getTags().stream()
                                .map(Enum::toString)
                                .toArray(String[]::new)))
                .switchIfEmpty(Mono.error(
                        new NotFoundException(valueOf(BLOG_POST_WITH_THESE_TAGS_NOT_FOUND))));
    }

    @Override
    public Mono<BlogPostRequest> updateBlogPost(BlogPostRequestUpdate blogPostRequest, String creatorEmail) {
        return blogPostRepository
                .existsBlogPostByTitleAndCreatorEmail(
                        blogPostRequest.oldTitle(),
                        creatorEmail)
                .flatMap(exists -> {
                    if (!exists){
                        return Mono.error(new BadRequestException(valueOf(BLOG_POST_NOT_FOUND)));
                    }else {
                        Mono<BlogPost> requestedBlogPost = blogPostRepository
                                .findByTitleAndCreatorEmail(
                                        blogPostRequest.oldTitle(),
                                        creatorEmail);
                        return requestedBlogPost
                                .flatMap(update -> {
                                    update.setTitle(blogPostRequest.newTitle());
                                    update.setContent(blogPostRequest.content());
                                    update.setPublishedAt(IDateTimeCreator.createDateTime());
                                    update.setTags(stream(blogPostRequest.tags())
                                            .map(request -> Tags.valueOf(request.toUpperCase()))
                                            .collect(Collectors.toSet()));

                                    return blogPostRepository
                                            .save(update)
                                            .map(response -> new BlogPostRequest(
                                                    response.getTitle(),
                                                    response.getContent(),
                                                    response.getCreatorEmail(),
                                                    response.getTags().stream()
                                                            .map(Enum::toString)
                                                            .toArray(String[]::new)))
                                            .log();
                                });
                    }
                });
    }

    @Override
    public Mono<Void> deleteBlogPostByTitleAndCreatorEmail(String title, String email) {
        return blogPostRepository
                .existsBlogPostByTitleAndCreatorEmail(title,email)
                .flatMap(exists -> {
                    if (!exists){
                        return Mono.error(
                                new BadRequestException(valueOf(BLOG_POST_NOT_FOUND)));
                    }else {
                        return blogPostRepository
                                .deleteBlogPostByTitleAndCreatorEmail(title,email)
                                .log();
                    }
                }).log();
    }
}