package com.jong.msa.board.endpoint.user.controller;

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
import com.jong.msa.board.endpoint.user.mapper.UserRequestMapper;
import com.jong.msa.board.endpoint.user.request.UserSessionModifyRequest;
import com.jong.msa.board.endpoint.user.request.UserSessionPasswordModifyRequest;
import com.jong.msa.board.endpoint.user.swagger.UserSessionOperations;
import com.jong.msa.board.endpoint.user.swagger.UserSessionOperations.UserSessionGetOperation;
import com.jong.msa.board.endpoint.user.swagger.UserSessionOperations.UserSessionModifyOperation;
import com.jong.msa.board.endpoint.user.swagger.UserSessionOperations.UserSessionPasswordModifyOperation;

import lombok.RequiredArgsConstructor;

@UserSessionOperations
@RestController
@RequiredArgsConstructor
@PreAuthorize(AccessExpression.IS_USER)
public class UserSessionRestController {

	private final Validator validator;

	private final UserRequestMapper requestMapper;
	
	private final MemberFeignClient memberFeignClient;

	private final RedisTemplate<String, String> redisTemplate;

	@UserSessionModifyOperation
	@PutMapping(value = "/apis/users",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyUserSession(
			@RequestBody UserSessionModifyRequest request) {
		
		MemberModifyRequest modifyRequest = requestMapper.toRequest(request);
		
		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(modifyRequest, validator);
		
		UUID sessionId = SecurityUtils.getSessionId();
		
		memberFeignClient.modifyMember(sessionId, modifyRequest);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@UserSessionPasswordModifyOperation
	@PatchMapping(value = "/apis/users/password",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyUserSessionPassword(
			@RequestBody UserSessionPasswordModifyRequest request) {
		
		MemberPasswordModifyRequest modifyPasswordRequest = requestMapper.toRequest(request);

		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(modifyPasswordRequest, validator);

		UUID sessionId = SecurityUtils.getSessionId();

		memberFeignClient.modifyMemberPassword(sessionId, modifyPasswordRequest);

		TokenUtils.revokeTokenAll(sessionId, redisTemplate);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

	}

	@UserSessionGetOperation
	@GetMapping(value = "/apis/users",
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MemberDetailsResponse> getUserSession() {
		
		return memberFeignClient.getMember(SecurityUtils.getSessionId());
	}

}
