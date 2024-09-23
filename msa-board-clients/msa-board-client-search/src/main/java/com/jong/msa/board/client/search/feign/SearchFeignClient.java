package com.jong.msa.board.client.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jong.msa.board.client.core.condition.FeignClientCondition;
import com.jong.msa.board.client.search.request.MemberSearchRequest;
import com.jong.msa.board.client.search.request.PostSearchRequest;
import com.jong.msa.board.client.search.response.MemberListResponse;
import com.jong.msa.board.client.search.response.PostListResponse;
import com.jong.msa.board.client.search.swagger.SearchOperations;
import com.jong.msa.board.client.search.swagger.SearchOperations.MemberListSearchOperation;
import com.jong.msa.board.client.search.swagger.SearchOperations.PostListSearchOperation;
import com.jong.msa.board.common.constants.MicroserviceNames;

@SearchOperations
@Conditional(FeignClientCondition.class)
@FeignClient(name = MicroserviceNames.SEARCH_MICROSERVICE)
public interface SearchFeignClient {

	@MemberListSearchOperation
	@PostMapping(value = "/apis/members/search",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MemberListResponse> searchMemberList(
			@RequestBody MemberSearchRequest request);
	
	@PostListSearchOperation
	@PostMapping(value = "/apis/posts/search",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PostListResponse> searchPostList(
			@RequestBody PostSearchRequest request);
	
}
