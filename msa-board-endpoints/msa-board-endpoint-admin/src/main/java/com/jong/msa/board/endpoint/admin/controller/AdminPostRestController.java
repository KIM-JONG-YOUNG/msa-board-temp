package com.jong.msa.board.endpoint.admin.controller;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jong.msa.board.client.core.exception.RestServiceException;
import com.jong.msa.board.client.core.utils.ValidationUtils;
import com.jong.msa.board.client.post.feign.PostFeignClient;
import com.jong.msa.board.client.post.request.PostCreateRequest;
import com.jong.msa.board.client.post.request.PostModifyRequest;
import com.jong.msa.board.client.post.response.PostDetailsResponse;
import com.jong.msa.board.client.search.feign.SearchFeignClient;
import com.jong.msa.board.client.search.request.PostSearchRequest;
import com.jong.msa.board.client.search.response.PostListResponse;
import com.jong.msa.board.common.constants.AccessExpression;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;
import com.jong.msa.board.common.enums.CodeEnum.Group;
import com.jong.msa.board.common.enums.CodeEnum.State;
import com.jong.msa.board.common.utils.StringUtils;
import com.jong.msa.board.endpoint.admin.mapper.AdminRequestMapper;
import com.jong.msa.board.endpoint.admin.request.AdminPostCreateRequest;
import com.jong.msa.board.endpoint.admin.request.AdminPostModifyRequest;
import com.jong.msa.board.endpoint.admin.request.AdminPostSearchRequest;
import com.jong.msa.board.endpoint.admin.swagger.AdminPostOperations;
import com.jong.msa.board.endpoint.admin.swagger.AdminPostOperations.AdminPostCreateOperation;
import com.jong.msa.board.endpoint.admin.swagger.AdminPostOperations.AdminPostGetOperation;
import com.jong.msa.board.endpoint.admin.swagger.AdminPostOperations.AdminPostListSearchOperation;
import com.jong.msa.board.endpoint.admin.swagger.AdminPostOperations.AdminPostModifyOperation;
import com.jong.msa.board.endpoint.admin.swagger.AdminPostOperations.AdminPostStateModifyOperation;

import lombok.RequiredArgsConstructor;

@AdminPostOperations
@RestController
@RequiredArgsConstructor
@PreAuthorize(AccessExpression.IS_ADMIN)
public class AdminPostRestController {

	private final Validator validator;
	
	private final AdminRequestMapper requestMapper;
	
	private final PostFeignClient postFeignClient;

	private final SearchFeignClient searchFeignClient;

	@AdminPostCreateOperation
	@PostMapping(value = "/apis/admins/posts",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> createPost(
			@RequestBody AdminPostCreateRequest request) {
		
		PostCreateRequest createRequest = requestMapper.toRequest(request);
		
		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(createRequest, validator);

		ResponseEntity<Void> createResponse = postFeignClient.createPost(createRequest);
		String location = createResponse.getHeaders().getLocation().toString();
		
		UUID id = UUID.fromString(location.substring(location.lastIndexOf("/") + 1));
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/admins/posts/", id))
				.build();
	}

	@AdminPostModifyOperation
	@PutMapping(value = "/apis/admins/posts/{id}",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyPost(
			@PathVariable(name = "id") UUID id,
			@RequestBody AdminPostModifyRequest request) {
		
		PostModifyRequest modifyRequest = requestMapper.toRequest(request);
		
		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(modifyRequest, validator);

		PostDetailsResponse post = postFeignClient.getPost(id).getBody();
		
		if (post.getWriter().getGroup() != Group.ADMIN) {
			throw new RestServiceException(HttpStatus.FORBIDDEN, ErrorCode.NOT_ADMIN_POST);
		} else {
			
			postFeignClient.modifyPost(id, modifyRequest);
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/admins/posts/", id))
					.build();
		}
	}
	
	@AdminPostStateModifyOperation
	@PatchMapping(value = "/apis/admins/posts/{id}/state",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyPostState(
			@PathVariable(name = "id") UUID id,
			@RequestBody State state) {
		
		postFeignClient.modifyPost(id, PostModifyRequest.builder().state(state).build());
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/admins/posts/", id))
				.build();
	}

	@AdminPostGetOperation
	@GetMapping(value = "/apis/admins/posts/{id}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PostDetailsResponse> getPost(
			@PathVariable(name = "id") UUID id) {
		
		return postFeignClient.getPost(id);
	}

	@AdminPostListSearchOperation
	@PreAuthorize(AccessExpression.IS_ADMIN)
	@PostMapping(value = "/apis/admins/posts/search",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PostListResponse> searchPostList(
			@RequestBody AdminPostSearchRequest request) {
		
		PostSearchRequest searchRequest = requestMapper.toRequest(request);
		
		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(searchRequest, validator);

		return searchFeignClient.searchPostList(searchRequest);
	}

}
