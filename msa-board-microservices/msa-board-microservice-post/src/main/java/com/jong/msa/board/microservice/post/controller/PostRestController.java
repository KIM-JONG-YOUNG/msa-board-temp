package com.jong.msa.board.microservice.post.controller;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RestController;

import com.jong.msa.board.client.core.utils.ValidationUtils;
import com.jong.msa.board.client.post.feign.PostFeignClient;
import com.jong.msa.board.client.post.request.PostCreateRequest;
import com.jong.msa.board.client.post.request.PostModifyRequest;
import com.jong.msa.board.client.post.response.PostDetailsResponse;
import com.jong.msa.board.common.utils.StringUtils;
import com.jong.msa.board.microservice.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostRestController implements PostFeignClient {

	private final Validator validator;
	
	private final PostService service;
	
	@Override
	public ResponseEntity<Void> createPost(PostCreateRequest request) {

		ValidationUtils.validateBy(request, validator);
		
		UUID id = service.createPost(request);
			
		return ResponseEntity.status(HttpStatus.CREATED)
				.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/members/", id))
				.build();
	}

	@Override
	public ResponseEntity<Void> modifyPost(UUID id, PostModifyRequest request) {

		ValidationUtils.validateBy(request, validator);

		service.modifyPost(id, request);
			
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/members/", id))
				.build();
	}

	@Override
	public ResponseEntity<Void> increasePostViews(UUID id) {

		service.increasePostViews(id);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.header(HttpHeaders.LOCATION, StringUtils.concat("/apis/members/", id))
				.build();
	}

	@Override
	public ResponseEntity<PostDetailsResponse> getPost(UUID id) {

		return ResponseEntity.status(HttpStatus.OK)
				.body(service.getPost(id));
	}

}
