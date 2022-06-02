package com.salenaluu.portfolio.blogpost.service;

import com.salenaluu.portfolio.blogpost.model.BlogPost;
import com.salenaluu.portfolio.blogpost.repository.IBlogPostRepository;
import com.salenaluu.portfolio.blogpost.utils.BlogPostSetupTest;
import com.salenaluu.portfolio.blogpost.utils.enums.Tags;
import com.salenaluu.portfolio.blogpost.utils.interfaces.IDateTimeCreator;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequest;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequestUpdate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BlogPostServiceImplTest {
    @Mock
    IBlogPostRepository blogPostRepository;

    @InjectMocks
    BlogPostServiceImpl blogPostService;

    @Spy
    BlogPostSetupTest blogPostSetupTest;

    String email = "test@example.com";

    // <editor-fold defaultstate="collapsed" desc="BlogPosts">
    BlogPost blogPost = new BlogPost(
            UUID.randomUUID(),
            "This is the way",
            "A special sentence with force in it.",
            IDateTimeCreator.createDateTime(),
            "salenaluu@gmail.com",
            new HashSet<>());

    BlogPostRequest blogPostRequest = new BlogPostRequest(
            "This is the way",
            "A special sentence with force in it.",
            "salenaluu@gmail.com",
            new String[]{});
    // </editor-fold>

    @BeforeEach
    void setup(){
        blogPostRepository
                .saveAll(blogPostSetupTest.blogPostList());
    }

    @AfterEach
    void tearDown(){
        blogPostRepository
                .deleteAll();
    }

    @Test
    @DisplayName("should createBlockPost()")
    void should_createBlockPost() {
        given(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.just(false));
        given(blogPostRepository.save(any()))
                .willReturn(Mono.just(blogPost));

        Mono<BlogPostRequest> requestedBlogPost = blogPostService.createBlogPost(blogPostRequest);

        StepVerifier
                .create(requestedBlogPost)
                .assertNext(check -> {
                    assertThat(check.title())
                            .isEqualTo("This is the way");
                    assertThat(check.content())
                            .isEqualTo("A special sentence with force in it.");
                    assertThat(check.creatorEmail())
                            .isEqualTo("salenaluu@gmail.com");})
                .verifyComplete();
    }

    @Test
    @DisplayName("should throw exception if BlogPost already exists by createBlockPost()")
    void should_throw_exception_if_BlogPost_already_exists_by_createBlockPost() {
        given(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.just(true));

        Mono<BlogPostRequest> requestedBlogPost = blogPostService.createBlogPost(blogPostRequest);

        StepVerifier
                .create(requestedBlogPost)
                .verifyErrorMessage("BLOG_POST_ALREADY_EXISTS");
    }

    @Test
    @DisplayName("should throw exception if RequestedModel is empty by createBlogPost()")
    void should_throw_exception_if_RequestedModel_is_empty_by_createBlogPost() {
        given(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.empty());

        BlogPostRequest emptyBlogPostRequest = new BlogPostRequest(
                "",
                "",
                "test.test.com",
                new String[]{});

        Mono<BlogPostRequest> requestedBlockPost = blogPostService.createBlogPost(emptyBlogPostRequest);

        StepVerifier
                .create(requestedBlockPost)
                .verifyErrorMessage("REQUEST_NOT_EXCEPTED");
    }

    @Test
    @DisplayName("should throw exception if RequestedModel is not excepted by createBlogPost()")
    void should_throw_exception_if_RequestedModel_is_not_excepted_by_createBlogPost() {
        given(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.just(false));
        given(blogPostRepository.save(any()))
                .willReturn(Mono.empty());

        Mono<BlogPostRequest> requestedBlogPost = blogPostService.createBlogPost(blogPostRequest);

        StepVerifier
                .create(requestedBlogPost)
                .verifyErrorMessage("REQUESTED_MODEL_INVALID");
    }

    @Test
    @DisplayName("should retrieve BlogPost by getBlogPostByTitleAndCreatorEmail()")
    void should_retrieve_BlogPost_by_getBlogPostByTitleAndCreatorEmail() {
        given(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.just(true));
        given(blogPostRepository.findByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.just(blogPost));

        Mono<BlogPostRequest> requestedBlogPost =
                blogPostService.getBlogPostByTitleAndCreatorEmail(
                        "This is the way",
                        "A special sentence with force in it.");

        StepVerifier
                .create(requestedBlogPost)
                .assertNext(check -> {
                    assertThat(check.title())
                            .isEqualTo("This is the way");
                    assertThat(check.content())
                            .isEqualTo("A special sentence with force in it.");
                    assertThat(check.creatorEmail())
                            .isEqualTo("salenaluu@gmail.com");})
                .verifyComplete();
    }

    @Test
    @DisplayName("should throw exception if BlogPost wasn't found by getBlogPostByTitleAndCreatorEmail()")
    void should_throw_exception_if_BlogPost_was_not_found_by_getBlogPostByTitleAndCreatorEmail() {
        given(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.just(false));

        Mono<BlogPostRequest> requestedBlogPost =
                blogPostService.getBlogPostByTitleAndCreatorEmail(
                        "This is the way",
                        "A special sentence with force in it.");

        StepVerifier
                .create(requestedBlogPost)
                .verifyErrorMessage("BLOG_POST_NOT_FOUND");
    }

    @Test
    @DisplayName("should getAllBlockPosts()")
    void should_getAllBlogPosts() {
        given(blogPostRepository.findAll())
                .willReturn(Flux.just(blogPost));

        Flux<BlogPostRequest> allBlogPosts = blogPostService.getAllBlogPosts();

        StepVerifier
                .create(allBlogPosts)
                .assertNext(check -> {
                    assertThat(check.title())
                            .isEqualTo("This is the way");
                    assertThat(check.creatorEmail())
                            .isEqualTo("salenaluu@gmail.com");})
                .verifyComplete();
    }

    @Test
    @DisplayName("should throw exception when BlogPostDB is empty by getAllBlogPosts()")
    void should_throw_exception_when_BlogPostDB_is_empty_by_getAllBlogPosts() {
        given(blogPostRepository.findAll())
                .willReturn(Flux.empty());

        Flux<BlogPostRequest> allBlogPosts = blogPostService.getAllBlogPosts();

        StepVerifier
                .create(allBlogPosts)
                .verifyErrorMessage("NO_CONTENT_IN_DB");
    }

    @Test
    @DisplayName("should getAllBlogPostsWithTag")
    void should_getAllBlogPostsWithTags() {
        // <editor-fold defaultstate="collapsed" desc="Extra BlogPost">
        BlogPost systemOfADown = new BlogPost(
                UUID.randomUUID(),
                "System Of A Down in Town",
                "Metal for every eares",
                IDateTimeCreator.createDateTime(),
                "blacklabel-metal@gmail.com",
                Set.of(Tags.FRESH));
        // </editor-fold>
        given(blogPostRepository.findAllByTags(any()))
                .willReturn(Flux.just(systemOfADown));

        Flux<BlogPostRequest> allBlogPostsWithTags =
                blogPostService.getAllBlogPostsWithTags(new String[]{"FRESH"});

        StepVerifier
                .create(allBlogPostsWithTags)
                .assertNext(check -> {
                    assertThat(check.title())
                            .isEqualTo("System Of A Down in Town");
                })
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("should throw exception if tags not match any BlogPost by getAllBlogPostsWithTags")
    void should_throw_exception_if_tags_not_match_any_Blogpost_by_getAllBlogPostsWithTags() {
        given(blogPostRepository.findAllByTags(any()))
                .willReturn(Flux.empty());

        Flux<BlogPostRequest> allBlogPostsWithTags =
                blogPostService.getAllBlogPostsWithTags(new String[]{"FRESH"});

        StepVerifier
                .create(allBlogPostsWithTags)
                .verifyErrorMessage("BLOG_POST_WITH_THESE_TAGS_NOT_FOUND");
    }

    @Test
    @DisplayName("should updateBlogPost()")
    void should_updateBlogPost() {
        given(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.just(true));
        given(blogPostRepository.findByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.just(blogPost));
        given(blogPostRepository.save(any()))
                .willReturn(Mono.just(blogPost));

        BlogPostRequestUpdate blogPostRequestUpdate =
                new BlogPostRequestUpdate(
                        "This is the way",
                        "This is a joke",
                        "Can you feel it?",
                        new String[]{"fresh","funny"});

        Mono<BlogPostRequest> requestedBlogPostUpdate = blogPostService.updateBlogPost(blogPostRequestUpdate,email);

        StepVerifier
                .create(requestedBlogPostUpdate)
                .assertNext(check -> {
                    assertThat(check.creatorEmail())
                            .isEqualTo("salenaluu@gmail.com");
                    assertThat(check.title())
                            .isEqualTo("This is a joke");
                    assertThat(check.tags().length)
                            .isEqualTo(2);})
                .verifyComplete();
    }

    @Test
    @DisplayName("should throw exception if BlogPost wasn't found by updateBlogPost()")
    void should_throw_Exception_if_BlogPost_was_not_found_by_updateBlogPost() {
        given(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.just(false));

        BlogPostRequestUpdate blogPostRequestUpdate =
                new BlogPostRequestUpdate(
                        "",
                        "This is a joke",
                        "Can you feel it?",
                        new String[]{"fresh","funny"});

        Mono<BlogPostRequest> requestedBlogPostUpdate = blogPostService.updateBlogPost(blogPostRequestUpdate,email);

        StepVerifier
                .create(requestedBlogPostUpdate)
                .verifyErrorMessage("BLOG_POST_NOT_FOUND");
    }

    @Test
    @DisplayName("should deleteBlogPostByTitleAndCreatorEmail()")
    void should_deleteBlogPostByTitleAndCreatorEmail() {
        given(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.just(true));
        given(blogPostRepository.deleteBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.empty());

        Mono<Void> requestedBlogPost =
                blogPostService.deleteBlogPostByTitleAndCreatorEmail(
                        blogPost.getTitle(),
                        blogPost.getCreatorEmail());

        StepVerifier
                .create(requestedBlogPost)
                .verifyComplete();
    }

    @Test
    @DisplayName("should exception when BlogPost wasn't found by deleteBlogPostByTitleAndCreatorEmail()")
    void should_throw_exception_when_BlogPost_was_not_found_by_deleteBlogPostByTitleAndCreatorEmail() {
        given(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .willReturn(Mono.just(false));

        Mono<Void> requestedBlogPost =
                blogPostService.deleteBlogPostByTitleAndCreatorEmail(
                        "",
                        blogPost.getCreatorEmail());

        StepVerifier
                .create(requestedBlogPost)
                .verifyErrorMessage("BLOG_POST_NOT_FOUND");
    }
}