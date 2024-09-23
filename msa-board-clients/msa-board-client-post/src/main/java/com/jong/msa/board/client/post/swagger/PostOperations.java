package com.jong.msa.board.client.post.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.http.HttpHeaders;

import com.jong.msa.board.client.core.swagger.ApiErrorResponses.ApiErrorResponse;
import com.jong.msa.board.client.post.response.PostDetailsResponse;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "게시글 마이크로서비스 API")
public @interface PostOperations {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "게시글 생성")
	@ApiResponse(responseCode = "201", headers = { 
			@Header(name = HttpHeaders.LOCATION, description = "게시글 URL") })
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.INVALID_PARAMETER)
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.NOT_FOUND_POST_WRITER)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	@ApiErrorResponse(responseCode = "502", errorCode = ErrorCode.UNCHECKED_EXTERNAL_ERROR)
	public @interface PostCreateOperation {}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "게시글 수정")
	@ApiResponse(responseCode = "204", headers = { 
			@Header(name = HttpHeaders.LOCATION, description = "게시글 URL") })
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.INVALID_PARAMETER)
	@ApiErrorResponse(responseCode = "404", errorCode = ErrorCode.NOT_FOUND_POST)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	public @interface PostModifyOperation {}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "게시글 조회수 증가")
	@ApiResponse(responseCode = "204", headers = { 
			@Header(name = HttpHeaders.LOCATION, description = "게시글 URL") })
	@ApiErrorResponse(responseCode = "404", errorCode = ErrorCode.NOT_FOUND_POST)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	public @interface PostViewsIncreaseOperation {}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "게시글 조회")
	@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = PostDetailsResponse.class)) })
	@ApiErrorResponse(responseCode = "404", errorCode = ErrorCode.NOT_FOUND_POST)
	@ApiErrorResponse(responseCode = "404", errorCode = ErrorCode.NOT_FOUND_POST_WRITER)
	@ApiErrorResponse(responseCode = "502", errorCode = ErrorCode.UNCHECKED_EXTERNAL_ERROR)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	public @interface PostGetOperation {}

}
