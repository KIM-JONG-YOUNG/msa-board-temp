
package com.jong.msa.board.microservice.member.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RestController;

import com.jong.msa.board.client.core.utils.ValidationUtils;
import com.jong.msa.board.client.member.feign.MemberFeignClient;
import com.jong.msa.board.client.member.request.MemberCreateRequest;
import com.jong.msa.board.client.member.request.MemberLoginRequest;
import com.jong.msa.board.client.member.request.MemberModifyRequest;
import com.jong.msa.board.client.member.request.MemberPasswordModifyRequest;
import com.jong.msa.board.client.member.response.MemberDetailsResponse;
import com.jong.msa.board.common.utils.StringUtils;
import com.jong.msa.board.microservice.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberRestController implements MemberFeignClient {
	
	@Autowired
	private final Validator validator;
	
	@Autowired
	private final MemberService service;
	
	@Override
	public ResponseEntity<Void> createMember(MemberCreateRequest request) {
		
		ValidationUtils.validateBy(request, validator);

		UUID id = service.createMember(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/members/", id))
				.build();
	}

	@Override
	public ResponseEntity<Void> modifyMember(UUID id, MemberModifyRequest request) {
		
		ValidationUtils.validateBy(request, validator);

		service.modifyMember(id, request);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/members/", id))
				.build();
	}

	@Override
	public ResponseEntity<Void> modifyMemberPassword(UUID id, MemberPasswordModifyRequest request) {

		ValidationUtils.validateBy(request, validator);

		service.modifyMemberPassword(id, request);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/members/", id))
				.build();
	}

	@Override
	public ResponseEntity<MemberDetailsResponse> getMember(UUID id) {

		return ResponseEntity.status(HttpStatus.OK)
				.body(service.getMember(id));
	}

	@Override
	public ResponseEntity<MemberDetailsResponse> loginMember(MemberLoginRequest request) {
		
		ValidationUtils.validateBy(request, validator);

		return ResponseEntity.status(HttpStatus.OK)
				.body(service.loginMember(request));
	}

}
