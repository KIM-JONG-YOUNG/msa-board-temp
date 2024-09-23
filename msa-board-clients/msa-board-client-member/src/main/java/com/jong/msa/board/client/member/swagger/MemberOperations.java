package com.jong.msa.board.client.member.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.http.HttpHeaders;

import com.jong.msa.board.client.core.swagger.ApiErrorResponses.ApiErrorResponse;
import com.jong.msa.board.client.member.response.MemberDetailsResponse;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "회원 마이크로서비스 API")
public @interface MemberOperations {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "회원 생성")
	@ApiResponse(responseCode = "201", headers = { 
			@Header(name = HttpHeaders.LOCATION, description = "회원 URL") })
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.INVALID_PARAMETER)
	@ApiErrorResponse(responseCode = "409", errorCode = ErrorCode.EXISTS_MEMBER_USERNAME)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	public @interface MemberCreateOperation {}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "회원 정보 수정")
	@ApiResponse(responseCode = "204", headers = { 
			@Header(name = HttpHeaders.LOCATION, description = "회원 URL") })
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.INVALID_PARAMETER)
	@ApiErrorResponse(responseCode = "404", errorCode = ErrorCode.NOT_FOUND_MEMBER)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	public @interface MemberModifyOperation {}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "회원 비밀번호 수정")
	@ApiResponse(responseCode = "204", headers = { 
			@Header(name = HttpHeaders.LOCATION, description = "회원 URL") })
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.INVALID_PARAMETER)
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.NOT_MATCHED_MEMBER_PASSWORD)
	@ApiErrorResponse(responseCode = "404", errorCode = ErrorCode.NOT_FOUND_MEMBER)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	public @interface MemberPasswordModifyOperation {}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "회원 조회")
	@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = MemberDetailsResponse.class)) })
	@ApiErrorResponse(responseCode = "404", errorCode = ErrorCode.NOT_FOUND_MEMBER)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	public @interface MemberGetOperation {}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "회원 로그인")
	@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = MemberDetailsResponse.class)) })
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.INVALID_PARAMETER)
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.NOT_FOUND_MEMBER_USERNAME)
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.NOT_ACTIVE_MEMBER_USERNAME)
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.NOT_MATCHED_MEMBER_PASSWORD)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	public @interface MemberLoginOperation {}

}
