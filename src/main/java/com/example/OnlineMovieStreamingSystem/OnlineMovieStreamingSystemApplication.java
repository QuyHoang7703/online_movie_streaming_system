package com.example.OnlineMovieStreamingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//disable security
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
//@SpringBootApplication
public class OnlineMovieStreamingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineMovieStreamingSystemApplication.class, args);
	}

}
