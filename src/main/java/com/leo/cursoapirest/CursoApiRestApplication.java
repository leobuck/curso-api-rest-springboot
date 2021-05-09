package com.leo.cursoapirest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = {"com.leo.cursoapirest.model"})
@ComponentScan(basePackages = {"com.leo.cursoapirest.*"})
@EnableJpaRepositories(basePackages = {"com.leo.cursoapirest.repository"})
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration
@EnableCaching
public class CursoApiRestApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(CursoApiRestApplication.class, args);
		ApplicationContextLoad applicationContextLoad = new ApplicationContextLoad();
		applicationContextLoad.setApplicationContext(applicationContext);
		System.out.println(new BCryptPasswordEncoder().encode("123"));
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/usuario/**")
			.allowedMethods("GET", "POST", "PUT", "DELETE")
			.allowedOrigins("http://localhost:4200");
		
		registry.addMapping("/profissao/**")
		.allowedMethods("GET", "POST", "PUT", "DELETE")
		.allowedOrigins("http://localhost:4200");
	}
}
