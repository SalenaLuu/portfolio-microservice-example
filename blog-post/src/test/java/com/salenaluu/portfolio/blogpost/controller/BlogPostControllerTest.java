package com.salenaluu.portfolio.blogpost.controller;

import com.salenaluu.portfolio.blogpost.model.BlogPost;
import com.salenaluu.portfolio.blogpost.repository.IBlogPostRepository;
import com.salenaluu.portfolio.blogpost.service.BlogPostServiceImpl;
import com.salenaluu.portfolio.blogpost.utils.enums.Tags;
import com.salenaluu.portfolio.blogpost.utils.interfaces.IDateTimeCreator;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequest;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostRequestUpdate;
import com.salenaluu.portfolio.blogpost.utils.mapper.BlogPostResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOpaqueToken;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@Import(BlogPostServiceImpl.class)
@WebFluxTest(controllers = BlogPostController.class)
class BlogPostControllerTest {
    @MockBean
    IBlogPostRepository blogPostRepository;
    @Autowired
    WebTestClient webTestClient;

    String baseUrl = "/api/v1/blogpost";

    // <editor-fold defaultstate="collapsed" desc="BlogPosts">
    BlogPost blogPost = new BlogPost(
            UUID.randomUUID(),
            "Scooby is Back!",
            "No way! And shaggy too ?",
            IDateTimeCreator.createDateTime(),
            "test@example.com",
            Set.of(Tags.FUNNY));

    BlogPostRequest blogPostRequest = new BlogPostRequest(
            "Scooby is Back!",
            "No way! And shaggy too ?",
            new String[]{"FUNNY"});

    BlogPostResponse blogPostResponse = new BlogPostResponse(
            "Scooby is Back!",
            "No way! And shaggy too ?",
            "test@example.com",
            new String[]{"FUNNY"});
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="New BlogPosts">
    BlogPostRequestUpdate blogPostRequestUpdate = new BlogPostRequestUpdate(
            "Scooby is Back!",
            "Shaggy is back!",
            "No way! And shaggy too ?",
            new String[]{"FUNNY"}
    );

    BlogPost newBlogPost = new BlogPost(
            blogPost.getId(),
            blogPostRequestUpdate.newTitle(),
            blogPostRequestUpdate.content(),
            IDateTimeCreator.createDateTime(),
            "test@example.com",
            Set.of(Tags.FUNNY));
    // </editor-fold>

    // TODO: FIX CONTROLLER POST REQUEST
    /*@Test
    @WithMockUser(authorities = {"portfolio_explorer"})
    @DisplayName("should createBlogPost()")
    void should_createBlogPost() {

        when(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.just(false));

        when(blogPostRepository.save(any()))
                .thenReturn(Mono.just(blogPost));

        webTestClient
                .mutateWith(mockOpaqueToken())
                .post()
                .uri(baseUrl)
                    .attribute("creatorEmail","test@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blogPostRequest)
                .exchange()
                .expectStatus()
                .isCreated();

        verify(blogPostRepository,times(1))
                .save(any());
        verify(blogPostRepository,times(1))
                .existsBlogPostByTitleAndCreatorEmail(anyString(),anyString());
    }

    @Test
    @DisplayName("should throw exception, if BlogPost already exists by createBlogPost()")
    void should_throw_exception_if_BlogPost_already_exists_by_createBlogPost(){

        when(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.just(true));

        webTestClient
                .post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blogPostRequest)
                .exchange()
                .expectStatus()
                .isBadRequest();

        verify(blogPostRepository,times(1))
                .existsBlogPostByTitleAndCreatorEmail(anyString(),anyString());

    }

    @Test
    @DisplayName("should throw exception, if BlogPostRequest is empty by createBlogPost()")
    void should_throw_exception_if_BlogPostRequest_is_empty_by_createBlogPost() {

        when(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.just(false));
        when(blogPostRepository.save(any()))
                .thenReturn(Mono.empty());

        webTestClient
                .post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blogPostRequest)
                .exchange()
                .expectStatus()
                .isBadRequest();

        verify(blogPostRepository,times(1))
                .existsBlogPostByTitleAndCreatorEmail(anyString(),anyString());

        verify(blogPostRepository,times(1))
                .save(any());
    }

    @Test
    @DisplayName("should throw exception, when title and email is null by createBlogPost()")
    void should_throw_exception_when_title_and_email_is_null_by_createBlogPost() {

        when(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.empty());

        webTestClient
                .post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(blogPostRequest)
                .exchange()
                .expectStatus()
                .isBadRequest();

        verify(blogPostRepository,times(1))
                .existsBlogPostByTitleAndCreatorEmail(anyString(),anyString());
    }


    @Test
    @DisplayName("should getBlogPostByTitleAndCreatorEmail()")
    void should_getBlogPostByTitleAndCreatorEmail() {
        when(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.just(true));
        when(blogPostRepository.findByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.just(blogPost));

        webTestClient
                .mutateWith(mockOpaqueToken())
                .get()
                .uri(baseUrl+"/{title}","Scooby is Back!")
                .exchange()
                .expectStatus()
                .isOk();

        verify(blogPostRepository,times(1))
                .existsBlogPostByTitleAndCreatorEmail(anyString(),anyString());

        verify(blogPostRepository,times(1))
                .findByTitleAndCreatorEmail(anyString(),anyString());
    }

    @Test
    @DisplayName("should throw exception, if BlogPost not found by getBlogPostByTitleAndCreatorEmail()")
    void should_throw_exception_if_BlogPost_not_found_by_getBlogPostByTitleAndCreatorEmail() {
        when(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.just(false));

        webTestClient
                .mutateWith(mockOpaqueToken())
                .get()
                .uri(baseUrl+"/{title}","Scooby is Back!")
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(blogPostRepository,times(1))
                .existsBlogPostByTitleAndCreatorEmail(anyString(),anyString());
    }

    @Test
    @DisplayName("should throw exception, if title is wrong or email is null by getBlogPostByTitleAndCreatorEmail()")
    void should_throw_exception_if_title_is_wrong_or_email_is_null_by_getBlogPostByTitleAndCreatorEmail() {
        when(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.empty());

        webTestClient
                .mutateWith(mockOpaqueToken())
                .get()
                .uri(baseUrl+"/{title}","wrong title")
                .exchange()
                .expectStatus()
                .isBadRequest();

        verify(blogPostRepository,times(1))
                .existsBlogPostByTitleAndCreatorEmail(anyString(),anyString());
    }*/

    @Test
    @DisplayName("should getAllBlogPosts()")
    void should_getAllBlogPosts() {
        when(blogPostRepository.findAll())
                .thenReturn(Flux.just(blogPost));

        webTestClient
                .mutateWith(mockOpaqueToken())
                .get()
                .uri(baseUrl)
                .exchange()
                .expectStatus()
                .isOk();

        verify(blogPostRepository,times(1))
                .findAll();
    }

    @Test
    @DisplayName("should throw exception, when BlogPost DB is empty by getAllBlogposts()")
    void should_throw_exception_when_BlogPost_DB_is_empty_by_getAllBlogposts() {
        when(blogPostRepository.findAll())
                .thenReturn(Flux.empty());

        webTestClient
                .mutateWith(mockOpaqueToken())
                .get()
                .uri(baseUrl)
                .exchange()
                .expectStatus()
                .isNoContent();

        verify(blogPostRepository,times(1))
                .findAll();
    }

    @Test
    @DisplayName("should getBlogPostByTags()")
    void should_getBlogPostByTags() {
        when(blogPostRepository.findAllByTags(any()))
                .thenReturn(Flux.just(blogPost));

        webTestClient
                .mutateWith(mockOpaqueToken())
                .get()
                .uri(baseUrl + "/filter?tags=FUNNY")
                .exchange()
                .expectStatus()
                .isOk();

        verify(blogPostRepository,times(1))
                .findAllByTags(any());
    }

    @Test
    @DisplayName("should throw exception, when BlogPost wasn't found by getBlogPostByTags()")
    void should_throw_exception_when_BlogPost_was_not_found_by_getBlogPostByTags() {
        when(blogPostRepository.findAllByTags(any()))
                .thenReturn(Flux.empty());

        webTestClient
                .mutateWith(mockOpaqueToken())
                .get()
                .uri(baseUrl + "/filter?tags=FRESH")
                .exchange()
                .expectStatus()
                .isNotFound();

        verify(blogPostRepository,times(1))
                .findAllByTags(any());
    }

    // TODO: FIX CONTROLLER PUT REQUEST
   /* @Test
    @DisplayName("should updateBlogPost()")
    void should_updateBlogPost() {
        when(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.just(true));
        when(blogPostRepository.findByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.just(blogPost));
        when(blogPostRepository.save(any()))
                .thenReturn(Mono.just(newBlogPost));


        webTestClient
                .mutateWith(mockOpaqueToken())
                .put()
                .uri(baseUrl+"/update")
                .bodyValue(blogPostRequestUpdate)
                .exchange()
                .expectStatus()
                .isOk();

        verify(blogPostRepository,times(1))
                .existsBlogPostByTitleAndCreatorEmail(anyString(),anyString());
        verify(blogPostRepository,times(1))
                .findByTitleAndCreatorEmail(anyString(),anyString());
    }

    @Test
    @DisplayName("should throw exception, when BlogPost wasn't found by updateBlogPost()")
    void should_throw_exception_when_BlogPost_was_not_found_by_updateBlogPost() {
        when(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.just(false));

        webTestClient
                .mutateWith(mockOpaqueToken())
                .put()
                .uri(baseUrl+"/update")
                .bodyValue(blogPostRequestUpdate)
                .exchange()
                .expectStatus()
                .isBadRequest();

        verify(blogPostRepository,times(1))
                .existsBlogPostByTitleAndCreatorEmail(anyString(),anyString());
    }*/


    // TODO: FIX CONTROLLER DELETE REQUEST
    /*@Test
    @DisplayName("should deleteBlogPostByTitleAndCreatorEmail()")
    void should_deleteBlogPostByTitleAndCreatorEmail() {
        when(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.just(true));
        when(blogPostRepository.deleteBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.empty());

        webTestClient
                .mutateWith(mockOpaqueToken())
                .delete()
                .uri(baseUrl + "?title=Scooby is Back!")
                .exchange()
                .expectStatus()
                .isNoContent();

        verify(blogPostRepository,times(1))
                .existsBlogPostByTitleAndCreatorEmail(anyString(),anyString());
        verify(blogPostRepository,times(1))
                .deleteBlogPostByTitleAndCreatorEmail(anyString(),anyString());
    }

    @Test
    @DisplayName("should throw exception when BlogPost wasn't found by deleteBlogPostByTitleAndCreatorEmail()")
    void should_throw_exception_when_BlogPost_was_not_found_by_deleteBlogPostByTitleAndCreatorEmail() {
        when(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
                .thenReturn(Mono.just(false));

        webTestClient
                .mutateWith(mockOpaqueToken())
                .delete()
                .uri(baseUrl + "?title=Scooby is ack!")
                .exchange()
                .expectStatus()
                .isBadRequest();

        verify(blogPostRepository,times(1))
                .existsBlogPostByTitleAndCreatorEmail(anyString(),anyString());
    }*/

}
