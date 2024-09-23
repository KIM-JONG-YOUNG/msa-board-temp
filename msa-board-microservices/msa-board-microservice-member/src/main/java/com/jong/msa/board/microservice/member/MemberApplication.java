package com.jong.msa.board.microservice.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jong.msa.board.client.core.exception.RestServiceException;
import com.jong.msa.board.client.member.request.MemberCreateRequest;
import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.common.constants.PackageNames;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;
import com.jong.msa.board.common.enums.CodeEnum.Gender;
import com.jong.msa.board.common.enums.CodeEnum.Group;
import com.jong.msa.board.microservice.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@EnableEurekaClient
@SpringBootApplication(scanBasePackages = PackageNames.ROOT_PACKAGE)
public class MemberApplication {

	public static void main(String[] args) {
		
		System.setProperty("spring.application.name", MicroserviceNames.MEMBER_MICROSERVICE);
		
		SpringApplication.run(MemberApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	@Slf4j
	@Component
	@RequiredArgsConstructor
	public static class Listener {
		
		private final MemberService service;
		
		public void listen(ApplicationReadyEvent event) {
			
			try {
				
				service.createMember(MemberCreateRequest.builder()
						.username("admin")
						.password("admin")
						.name("관리자")
						.gender(Gender.MAIL)
						.email("admin@example.com")
						.group(Group.ADMIN)
						.build());
				
			} catch (RestServiceException e) {
				
				if (e.getErrorCode() != ErrorCode.EXISTS_MEMBER_USERNAME) {
					log.error(e.getMessage(), e);
				}
			}
		}
		
	}
	
}
