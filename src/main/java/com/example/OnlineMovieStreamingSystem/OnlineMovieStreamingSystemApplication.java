package com.example.OnlineMovieStreamingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

//disable security
//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class OnlineMovieStreamingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineMovieStreamingSystemApplication.class, args);
	}

}
