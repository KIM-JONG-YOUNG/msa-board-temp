package com.jong.msa.board.endpoint.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.common.constants.PackageNames;

@EnableEurekaClient
@SpringBootApplication(scanBasePackages = PackageNames.ROOT_PACKAGE)
public class AdminApplication {

	public static void main(String[] args) {
		
		System.setProperty("spring.application.name", MicroserviceNames.ADMIN_ENDPOINT);
		
		SpringApplication.run(AdminApplication.class, args);
	}
	
	
	
}
