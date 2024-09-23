package com.jong.msa.board.endpoint.user.controller;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jong.msa.board.client.core.exception.RestServiceException;
import com.jong.msa.board.client.core.utils.SecurityUtils;
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
import com.jong.msa.board.common.enums.CodeEnum.State;
import com.jong.msa.board.common.utils.StringUtils;
import com.jong.msa.board.endpoint.user.mapper.UserRequestMapper;
import com.jong.msa.board.endpoint.user.request.UserPostCreateRequest;
import com.jong.msa.board.endpoint.user.request.UserPostModifyRequest;
import com.jong.msa.board.endpoint.user.request.UserPostSearchRequest;
import com.jong.msa.board.endpoint.user.swagger.UserPostOperations;
import com.jong.msa.board.endpoint.user.swagger.UserPostOperations.UserPostCreateOperation;
import com.jong.msa.board.endpoint.user.swagger.UserPostOperations.UserPostGetOperation;
import com.jong.msa.board.endpoint.user.swagger.UserPostOperations.UserPostListSearchOperation;
import com.jong.msa.board.endpoint.user.swagger.UserPostOperations.UserPostModifyOperation;
import com.jong.msa.board.endpoint.user.swagger.UserPostOperations.UserPostRemoveOperation;

import lombok.RequiredArgsConstructor;

@UserPostOperations
@RestController
@RequiredArgsConstructor
@PreAuthorize(AccessExpression.IS_USER)
public class UserPostRestController {

	private final Validator validator;

	private final UserRequestMapper requestMapper;
	
	private final PostFeignClient postFeignClient;

	private final SearchFeignClient searchFeignClient;

	@UserPostCreateOperation
	@PostMapping(value = "/apis/users/posts",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> createPost(
			@RequestBody UserPostCreateRequest request) {
		
		PostCreateRequest createRequest = requestMapper.toRequest(request);
		
		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(createRequest, validator);

		ResponseEntity<Void> createResponse = postFeignClient.createPost(createRequest);
		String location = createResponse.getHeaders().getLocation().toString();
		
		UUID id = UUID.fromString(location.substring(location.lastIndexOf("/") + 1));
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/users/posts/", id))
				.build();
	}

	@UserPostModifyOperation
	@PutMapping(value = "/apis/users/posts/{id}",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyPost(
			@PathVariable(name = "id") UUID id,
			@RequestBody UserPostModifyRequest request) {
		
		PostModifyRequest modifyRequest = requestMapper.toRequest(request);
		
		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(modifyRequest, validator);
		
		PostDetailsResponse post = postFeignClient.getPost(id).getBody();

		UUID sessionId = SecurityUtils.getSessionId();
		
		if (post.getState() != State.ACTIVE) {
			throw new RestServiceException(HttpStatus.GONE, ErrorCode.NOT_ACTIVE_POST);
		} else if (post.getWriter().getId().equals(sessionId) == false) {
			throw new RestServiceException(HttpStatus.FORBIDDEN, ErrorCode.NOT_POST_WRITER);
		} else {
			
			postFeignClient.modifyPost(id, modifyRequest);
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/users/posts/", id))
					.build();
		}
	}

	@UserPostRemoveOperation
	@DeleteMapping(value = "/apis/users/posts/{id}")
	ResponseEntity<Void> removePost(
			@PathVariable(name = "id") UUID id) {
		
		PostDetailsResponse post = postFeignClient.getPost(id).getBody();

		UUID sessionId = SecurityUtils.getSessionId();

		if (post.getState() != State.ACTIVE) {
			throw new RestServiceException(HttpStatus.GONE, ErrorCode.NOT_ACTIVE_POST);
		} else if (post.getWriter().getId().equals(sessionId) == false) {
			throw new RestServiceException(HttpStatus.FORBIDDEN, ErrorCode.NOT_POST_WRITER);
		} else {
			
			postFeignClient.modifyPost(id, PostModifyRequest.builder().state(State.DEACTIVE).build());
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}

	@UserPostGetOperation
	@GetMapping(value = "/apis/users/posts/{id}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PostDetailsResponse> getPost(
			@PathVariable(name = "id") UUID id) {
		
		ResponseEntity<PostDetailsResponse> postResponse = postFeignClient.getPost(id);

		if (postResponse.getBody().getState() != State.ACTIVE) {
			throw new RestServiceException(HttpStatus.GONE, ErrorCode.NOT_ACTIVE_POST);
		} else {

			postFeignClient.increasePostViews(id);
			
			return postResponse;
		}
	}

	@UserPostListSearchOperation
	@PostMapping(value = "/apis/users/posts/search",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PostListResponse> searchPostList(
			@RequestBody UserPostSearchRequest request) {
		
		PostSearchRequest searchRequest = requestMapper.toRequest(request);
		
		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(searchRequest, validator);

		return searchFeignClient.searchPostList(searchRequest);
	}
	
}
