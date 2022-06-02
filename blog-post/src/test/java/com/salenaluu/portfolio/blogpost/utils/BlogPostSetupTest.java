package com.salenaluu.portfolio.blogpost.utils;

import com.salenaluu.portfolio.blogpost.model.BlogPost;
import com.salenaluu.portfolio.blogpost.utils.enums.Tags;
import com.salenaluu.portfolio.blogpost.utils.interfaces.IDateTimeCreator;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BlogPostSetupTest {
    @Bean
    public List<BlogPost> blogPostList(){
        // Here we create our BlogPosts to test
        return List.of(
                new BlogPost(
                        UUID.randomUUID(),
                        "The Weather Girls in New York City",
                        "It's raining men a classic song of the 80s",
                        IDateTimeCreator.createDateTime(),
                        "soul-sisters@gmail.com",
                        Set.of(Tags.FRESH,Tags.FUNNY)
                ),
                new BlogPost(
                        UUID.randomUUID(),
                        "The new Queen Lizzo",
                        "The new Song About Damn Time is out!",
                        IDateTimeCreator.createDateTime(),
                        "lizzo@gmail.com",
                        Set.of(Tags.FRESH)));
    }
}