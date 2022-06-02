package com.salenaluu.portfolio.blogpost.repository;

import com.salenaluu.portfolio.blogpost.model.BlogPost;
import com.salenaluu.portfolio.blogpost.utils.BlogPostSetupTest;
import com.salenaluu.portfolio.blogpost.utils.enums.Tags;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
class IBlogPostRepositoryTest {
    @Autowired
    IBlogPostRepository IBlogPostRepository;
    @Spy
    BlogPostSetupTest blogPostSetupTest;

    @BeforeEach
    void setup(){
        IBlogPostRepository
                .saveAll(blogPostSetupTest.blogPostList())
                .blockLast();
    }

    @AfterEach
    void tearDown(){
        IBlogPostRepository
                .deleteAll()
                .block();
    }

    @Test
    @DisplayName("should retrieve BlogPost by findByTitleAndCreatorEmail() ")
    void should_retrieve_BlogPost_by_findByTitleAndCreatorEmail() {
        Mono<BlogPost> requestedBlogPost =
                IBlogPostRepository.findByTitleAndCreatorEmail(
                        "The Weather Girls in New York City",
                        "soul-sisters@gmail.com");

        StepVerifier
                .create(requestedBlogPost)
                .assertNext(
                        check -> {
                            assertThat(check)
                                    .isNotNull();
                            assertThat(check.getTitle())
                                    .isEqualTo("The Weather Girls in New York City");
                            assertThat(check.getCreatorEmail())
                                    .isEqualTo("soul-sisters@gmail.com");
                        })
                .verifyComplete();
    }

    @Test
    @DisplayName("should check, if any Blogpost was founded by existsBlogPostByTitleAndCreatorEmail()")
    void should_check_if_any_Blogpost_was_founded_by_existsBlogPostByTitleAndCreatorEmail() {
        Mono<Boolean> requestedBlogPost =
                IBlogPostRepository.existsBlogPostByTitleAndCreatorEmail(
                        "The new Queen Lizzo",
                        "lizzo@gmail.com");

        StepVerifier
                .create(requestedBlogPost)
                .assertNext(check -> {
                    assertThat(check)
                            .isNotNull();
                    assertThat(check.booleanValue())
                            .isTrue();})
                .verifyComplete();
    }

    @Test
    @DisplayName("should delete BlogPost by deleteBlogPostByTitleAndCreatorEmail()")
    void should_delete_Blogpost_by_deleteBlogPostByTitleAndCreatorEmail() {
        Mono<Void> requestedBlogPost =
                IBlogPostRepository.deleteBlogPostByTitleAndCreatorEmail(
                        "The Weather Girls in New York City",
                        "soul-sisters@gmail.com");

        StepVerifier
                .create(requestedBlogPost)
                .verifyComplete();
    }

    @Test
    @DisplayName("should findBlogPostByTags()")
    void should_findBlogPostByTags() {
        Flux<BlogPost> requestedVBlogPost =
                IBlogPostRepository.findAllByTags(Set.of(Tags.FRESH,Tags.FUNNY));

        StepVerifier
                .create(requestedVBlogPost)
                .assertNext(check -> {
                    assertThat(check.getTitle())
                            .isEqualTo("The Weather Girls in New York City");
                })
                .verifyComplete();
    }
}