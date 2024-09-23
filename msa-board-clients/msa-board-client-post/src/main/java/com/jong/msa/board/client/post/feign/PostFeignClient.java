package com.jong.msa.board.client.post.feign;

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
import com.jong.msa.board.client.post.request.PostCreateRequest;
import com.jong.msa.board.client.post.request.PostModifyRequest;
import com.jong.msa.board.client.post.response.PostDetailsResponse;
import com.jong.msa.board.client.post.swagger.PostOperations;
import com.jong.msa.board.client.post.swagger.PostOperations.PostCreateOperation;
import com.jong.msa.board.client.post.swagger.PostOperations.PostGetOperation;
import com.jong.msa.board.client.post.swagger.PostOperations.PostModifyOperation;
import com.jong.msa.board.client.post.swagger.PostOperations.PostViewsIncreaseOperation;
import com.jong.msa.board.common.constants.MicroserviceNames;

@PostOperations
@Conditional(FeignClientCondition.class)
@FeignClient(name = MicroserviceNames.POST_MICROSERVICE)
public interface PostFeignClient {

	@PostCreateOperation
	@PostMapping(value = "/apis/posts",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> createPost(
			@RequestBody PostCreateRequest request);

	@PostModifyOperation
	@PutMapping(value = "/apis/posts/{id}",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> modifyPost(
			@PathVariable(name = "id") UUID id,
			@RequestBody PostModifyRequest request);

	@PostViewsIncreaseOperation
	@PatchMapping(value = "/apis/posts/{id}/views/increase",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> increasePostViews(
			@PathVariable(name = "id") UUID id);

	@PostGetOperation
	@GetMapping(value = "/apis/posts/{id}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PostDetailsResponse> getPost(
			@PathVariable(name = "id") UUID id);
	
}
