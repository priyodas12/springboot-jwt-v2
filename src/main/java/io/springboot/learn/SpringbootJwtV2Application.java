package io.springboot.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@SpringBootApplication
public class SpringbootJwtV2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootJwtV2Application.class, args);
	}


	@Bean
	PasswordEncoder getPasswordEncoder(){
		return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A);
	}
}
