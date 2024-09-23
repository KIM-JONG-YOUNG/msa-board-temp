package com.jong.msa.board.client.member.feign;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jong.msa.board.client.core.condition.FeignClientCondition;
import com.jong.msa.board.client.member.request.MemberCreateRequest;
import com.jong.msa.board.client.member.request.MemberLoginRequest;
import com.jong.msa.board.client.member.request.MemberModifyRequest;
import com.jong.msa.board.client.member.request.MemberPasswordModifyRequest;
import com.jong.msa.board.client.member.response.MemberDetailsResponse;
import com.jong.msa.board.client.member.swagger.MemberOperations;
import com.jong.msa.board.client.member.swagger.MemberOperations.MemberCreateOperation;
import com.jong.msa.board.client.member.swagger.MemberOperations.MemberGetOperation;
import com.jong.msa.board.client.member.swagger.MemberOperations.MemberLoginOperation;
import com.jong.msa.board.client.member.swagger.MemberOperations.MemberModifyOperation;
import com.jong.msa.board.client.member.swagger.MemberOperations.MemberPasswordModifyOperation;
import com.jong.msa.board.common.constants.MicroserviceNames;

@MemberOperations
@Conditional(FeignClientCondition.class)
@FeignClient(name = MicroserviceNames.MEMBER_MICROSERVICE)
public interface MemberFeignClient {

	@MemberCreateOperation
	@PostMapping(value = "/apis/members",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> createMember(
			@RequestBody MemberCreateRequest request);
	
	@MemberModifyOperation
	@PutMapping(value = "/apis/members/{id}",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyMember(
			@PathVariable(name = "id") UUID id,
			@RequestBody MemberModifyRequest request);

	@MemberPasswordModifyOperation
	@PatchMapping(value = "/apis/members/{id}/password",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyMemberPassword(
			@PathVariable(name = "id") UUID id,
			@RequestBody MemberPasswordModifyRequest request);

	@MemberGetOperation
	@GetMapping(value = "/apis/members/{id}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MemberDetailsResponse> getMember(
			@PathVariable(name = "id") UUID id);

	@MemberLoginOperation
	@PostMapping(value = "/apis/members/login",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MemberDetailsResponse> loginMember(
			@RequestBody MemberLoginRequest request);

}
