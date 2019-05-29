package com.EMS;

import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.EMS.model.UserModel;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class TimetrackerApplication  	//extends SpringBootServletInitializer

{

	
	public static void main(String[] args) {
		SpringApplication.run(TimetrackerApplication.class, args);
	}

	
	
	@Bean
	public ObjectMapper configureObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		// set properties for objectmapper here

		return mapper;
	}
	
	 @Bean
	    public PasswordEncoder passwordEncoder() {
	        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	    }
	 
}

@Configuration
@EnableJpaAuditing
class DataJpaConfig {

    @Bean
    public AuditorAware<UserModel> auditor() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .map(UserModel.class::cast);
    }
}
