package com.jong.msa.board.client.search.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jong.msa.board.client.core.swagger.ApiErrorResponses.ApiErrorResponse;
import com.jong.msa.board.client.search.response.MemberListResponse;
import com.jong.msa.board.client.search.response.PostListResponse;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "검색 마이크로서비스 API")
public @interface SearchOperations {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "회원 검색")
	@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = MemberListResponse.class)) })
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.INVALID_PARAMETER)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	public @interface MemberListSearchOperation {}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "게시글 검색")
	@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = PostListResponse.class)) })
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.INVALID_PARAMETER)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	public @interface PostListSearchOperation {}
	
}
