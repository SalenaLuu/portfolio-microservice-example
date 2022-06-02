package com.salenaluu.portfolio.blogpost;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class BlogPostApplicationTests {
	@Test
	void contextLoads() {
		BlogPostApplication blogPostApplication = new BlogPostApplication();
		assertThat(blogPostApplication).isNotNull();
	}
}
