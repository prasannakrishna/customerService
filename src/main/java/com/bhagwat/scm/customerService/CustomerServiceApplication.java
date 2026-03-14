package com.bhagwat.scm.customerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
		"com.bhagwat.scm.customerService.command.repository",
		"com.bhagwat.scm.customerService.query.repository"
})

@ComponentScan(basePackages = {
		"com.bhagwat.scm.customerService"
})
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

}
