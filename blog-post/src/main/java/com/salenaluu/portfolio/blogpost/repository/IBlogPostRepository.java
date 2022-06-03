package com.salenaluu.portfolio.blogpost.repository;

import com.salenaluu.portfolio.blogpost.model.BlogPost;
import com.salenaluu.portfolio.blogpost.utils.enums.Tags;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public interface IBlogPostRepository extends ReactiveMongoRepository<BlogPost, UUID> {
    Mono<BlogPost> findByTitleAndCreatorEmail(String title, String email);
    Mono<Boolean> existsBlogPostByTitleAndCreatorEmail(String title, String email);
    Mono<Void> deleteBlogPostByTitleAndCreatorEmail(String title, String email);
    Flux<BlogPost> findAllByTags(Set<Tags> tags);
}