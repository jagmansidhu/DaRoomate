package com.roomate.app;

import com.roomate.app.domain.RequestContext;
import com.roomate.app.entity.RoleEntity;
import com.roomate.app.enumeration.Permissions;
import com.roomate.app.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class StartOneApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartOneApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
		return args -> {
//			RequestContext.setUserId(0L);
//			var userRole = new RoleEntity();
//			userRole.setRoleName(Permissions.USER.name());
//			userRole.setAuthorities(Permissions.USER);
//			roleRepository.save(userRole);
//
//			var adminRole = new RoleEntity();
//			adminRole.setRoleName(Permissions.ADMIN.name());
//			adminRole.setAuthorities(Permissions.ADMIN);
//			roleRepository.save(adminRole);
//			RequestContext.start();
		};
	}
}
