package com.jong.msa.board.endpoint.user.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jong.msa.board.client.core.swagger.ApiErrorResponses.ApiErrorResponse;
import com.jong.msa.board.common.constants.HeaderNames;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "일반 회원 인증 API")
public @interface UserAuthOperations {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "일반 회원 로그인")
	@ApiResponse(responseCode = "204", headers = {
			@Header(name = HeaderNames.ACCESS_TOKEN, description = "Access Token"),
			@Header(name = HeaderNames.REFRESH_TOKEN, description = "Refresh Token") })
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.INVALID_PARAMETER)
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.NOT_FOUND_MEMBER_USERNAME)
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.NOT_ACTIVE_MEMBER_USERNAME)
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.NOT_USER_GROUP_MEMBER_USERNAME)
	@ApiErrorResponse(responseCode = "400", errorCode = ErrorCode.NOT_MATCHED_MEMBER_PASSWORD)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	@ApiErrorResponse(responseCode = "502", errorCode = ErrorCode.UNCHECKED_EXTERNAL_ERROR)
	public @interface UserLoginOperation {}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "일반 회원 로그아웃")
	@ApiResponse(responseCode = "204")
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	public @interface UserLogoutOperation {}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "일반 회원 토큰 재발급")
	@ApiResponse(responseCode = "204", headers = {
			@Header(name = HeaderNames.ACCESS_TOKEN, description = "New Access Token"),
			@Header(name = HeaderNames.REFRESH_TOKEN, description = "New Refresh Token") })
	@ApiErrorResponse(responseCode = "401", errorCode = ErrorCode.EXPIRED_REFRESH_TOKEN)
	@ApiErrorResponse(responseCode = "401", errorCode = ErrorCode.REVOKED_REFRESH_TOKEN)
	@ApiErrorResponse(responseCode = "401", errorCode = ErrorCode.INVALID_REFRESH_TOKEN)
	@ApiErrorResponse(responseCode = "401", errorCode = ErrorCode.NOT_USER_GROUP_MEMBER_REFRESH_TOKEN)
	@ApiErrorResponse(responseCode = "404", errorCode = ErrorCode.NOT_FOUND_MEMBER)
	@ApiErrorResponse(responseCode = "500", errorCode = ErrorCode.UNCHECKED_INTERNAL_ERROR)
	@ApiErrorResponse(responseCode = "502", errorCode = ErrorCode.UNCHECKED_EXTERNAL_ERROR)
	public @interface UserRefreshOperation {}
	
}
