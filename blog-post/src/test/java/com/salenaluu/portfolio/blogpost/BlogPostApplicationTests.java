package com.salenaluu.portfolio.blogpost;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BlogPostApplicationTests {
	@Test
	void contextLoads() {
		BlogPostApplication blogPostApplication = new BlogPostApplication();
		assertThat(blogPostApplication).isNotNull();
	}
}
