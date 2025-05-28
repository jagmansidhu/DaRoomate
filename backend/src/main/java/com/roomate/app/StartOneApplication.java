package com.roomate.app;

import com.roomate.app.entities.AddressEntity;
import com.roomate.app.entities.UserEntity;
import com.roomate.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StartOneApplication {
	private static final Logger log = LoggerFactory.getLogger(StartOneApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(StartOneApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(UserRepository repository) {
		return (args) -> {
			// save a few customers
//			repository.save(new UserEntity("Jack", "Bauer", "Chese@gmail.com", " "));
//			repository.save(new UserEntity("Chloe", "O'Brian", "Obrian@gmail.com", "1234567890"));
		};
	}
}
