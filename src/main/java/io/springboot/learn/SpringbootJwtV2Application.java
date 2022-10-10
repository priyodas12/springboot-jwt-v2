package io.springboot.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class SpringbootJwtV2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootJwtV2Application.class, args);
	}

}
