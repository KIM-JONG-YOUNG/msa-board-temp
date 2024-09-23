package com.jong.msa.board.endpoint.admin.controller;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jong.msa.board.client.core.utils.SecurityUtils;
import com.jong.msa.board.client.core.utils.TokenUtils;
import com.jong.msa.board.client.core.utils.ValidationUtils;
import com.jong.msa.board.client.member.feign.MemberFeignClient;
import com.jong.msa.board.client.member.request.MemberModifyRequest;
import com.jong.msa.board.client.member.request.MemberPasswordModifyRequest;
import com.jong.msa.board.client.member.response.MemberDetailsResponse;
import com.jong.msa.board.common.constants.AccessExpression;
import com.jong.msa.board.endpoint.admin.mapper.AdminRequestMapper;
import com.jong.msa.board.endpoint.admin.request.AdminSessionModifyRequest;
import com.jong.msa.board.endpoint.admin.request.AdminSessionPasswordModifyRequest;
import com.jong.msa.board.endpoint.admin.swagger.AdminSessionOperations;
import com.jong.msa.board.endpoint.admin.swagger.AdminSessionOperations.AdminSessionGetOperation;
import com.jong.msa.board.endpoint.admin.swagger.AdminSessionOperations.AdminSessionModifyOperation;
import com.jong.msa.board.endpoint.admin.swagger.AdminSessionOperations.AdminSessionPasswordModifyOperation;

import lombok.RequiredArgsConstructor;

@AdminSessionOperations
@RestController
@RequiredArgsConstructor
public class AdminSessionRestController {

	private final Validator validator;
	
	private final AdminRequestMapper requestMapper;
	
	private final MemberFeignClient memberFeignClient;
	
	private final RedisTemplate<String, String> redisTemplate;
	
	@AdminSessionModifyOperation
	@PreAuthorize(AccessExpression.IS_ADMIN)
	@PutMapping(value = "/apis/admins",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyAdminSession(
			@RequestBody AdminSessionModifyRequest request) {
		
		MemberModifyRequest modifyRequest = requestMapper.toRequest(request);
		
		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(modifyRequest, validator);
		
		UUID sessionId = SecurityUtils.getSessionId();
		
		memberFeignClient.modifyMember(sessionId, modifyRequest);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@AdminSessionPasswordModifyOperation
	@PreAuthorize(AccessExpression.IS_ADMIN)
	@PatchMapping(value = "/apis/admins/password",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyAdminSessionPassword(
			@RequestBody AdminSessionPasswordModifyRequest request) {
		
		MemberPasswordModifyRequest modifyPasswordRequest = requestMapper.toRequest(request);
		
		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(modifyPasswordRequest, validator);
		
		UUID sessionId = SecurityUtils.getSessionId();

		memberFeignClient.modifyMemberPassword(sessionId, modifyPasswordRequest);

		TokenUtils.revokeTokenAll(sessionId, redisTemplate);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@AdminSessionGetOperation
	@PreAuthorize(AccessExpression.IS_ADMIN)
	@GetMapping(value = "/apis/admins",
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MemberDetailsResponse> getAdminSession() {
		
		return memberFeignClient.getMember(SecurityUtils.getSessionId());
	}

}
