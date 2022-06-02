package com.salenaluu.portfolio.eurekaserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class EurekaServerApplicationTests {

	@Test
	void contextLoads() {
		EurekaServerApplication eurekaServerApplication = new EurekaServerApplication();
		assertThat(eurekaServerApplication).isNotNull();
	}

}
