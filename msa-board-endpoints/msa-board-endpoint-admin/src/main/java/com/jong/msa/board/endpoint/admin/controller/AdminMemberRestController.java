package com.jong.msa.board.endpoint.admin.controller;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jong.msa.board.client.core.exception.RestServiceException;
import com.jong.msa.board.client.core.utils.TokenUtils;
import com.jong.msa.board.client.core.utils.ValidationUtils;
import com.jong.msa.board.client.member.feign.MemberFeignClient;
import com.jong.msa.board.client.member.request.MemberModifyRequest;
import com.jong.msa.board.client.member.response.MemberDetailsResponse;
import com.jong.msa.board.client.search.feign.SearchFeignClient;
import com.jong.msa.board.client.search.request.MemberSearchRequest;
import com.jong.msa.board.client.search.response.MemberListResponse;
import com.jong.msa.board.common.constants.AccessExpression;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;
import com.jong.msa.board.common.enums.CodeEnum.Group;
import com.jong.msa.board.common.utils.StringUtils;
import com.jong.msa.board.endpoint.admin.mapper.AdminRequestMapper;
import com.jong.msa.board.endpoint.admin.request.AdminMemberSearchRequest;
import com.jong.msa.board.endpoint.admin.request.AdminUserModifyRequest;
import com.jong.msa.board.endpoint.admin.swagger.AdminMemberOperations;
import com.jong.msa.board.endpoint.admin.swagger.AdminMemberOperations.AdminMemberGetOperation;
import com.jong.msa.board.endpoint.admin.swagger.AdminMemberOperations.AdminMemberListSearchOperation;
import com.jong.msa.board.endpoint.admin.swagger.AdminMemberOperations.AdminUserModifyOperation;

import lombok.RequiredArgsConstructor;

@AdminMemberOperations
@RestController
@RequiredArgsConstructor
@PreAuthorize(AccessExpression.IS_ADMIN)
public class AdminMemberRestController {

	private final Validator validator;
	
	private final AdminRequestMapper requestMapper;
	
	private final MemberFeignClient memberFeignClient;

	private final SearchFeignClient searchFeignClient;

	private final RedisTemplate<String, String> redisTemplate;
	
	@AdminUserModifyOperation
	@PutMapping(value = "/apis/admins/users/{id}",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyUser(
			@PathVariable(name = "id") UUID id,
			@RequestBody AdminUserModifyRequest request) {

		MemberModifyRequest modifyRequest = requestMapper.toRequest(request);
		
		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(modifyRequest, validator);

		MemberDetailsResponse member = memberFeignClient.getMember(id).getBody();

		if (member.getGroup() != Group.USER) {
			throw new RestServiceException(HttpStatus.FORBIDDEN, ErrorCode.NOT_USER_GROUP_MEMBER);
		} else {
			
			memberFeignClient.modifyMember(id, modifyRequest);

			TokenUtils.revokeTokenAll(id, redisTemplate);
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/admins/members/", id))
					.build();
		}
	}

	@AdminMemberGetOperation
	@GetMapping(value = "/apis/admins/members/{id}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MemberDetailsResponse> getMember(
			@PathVariable(name = "id") UUID id) {
		
		return memberFeignClient.getMember(id);
	}

	@AdminMemberListSearchOperation
	@PostMapping(value = "/apis/admins/members/search",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MemberListResponse> searchMemberList(
			@RequestBody AdminMemberSearchRequest request) {
		
		MemberSearchRequest searchRequest = requestMapper.toRequest(request);
		
		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(searchRequest, validator);
		
		return searchFeignClient.searchMemberList(searchRequest);
	}

}
