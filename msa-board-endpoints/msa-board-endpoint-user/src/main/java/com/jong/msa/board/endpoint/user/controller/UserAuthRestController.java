package com.jong.msa.board.endpoint.user.controller;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.jong.msa.board.client.core.exception.RestServiceException;
import com.jong.msa.board.client.core.exception.RevokedJwtException;
import com.jong.msa.board.client.core.properties.TokenProperties;
import com.jong.msa.board.client.core.utils.TokenUtils;
import com.jong.msa.board.client.core.utils.ValidationUtils;
import com.jong.msa.board.client.member.feign.MemberFeignClient;
import com.jong.msa.board.client.member.request.MemberLoginRequest;
import com.jong.msa.board.client.member.response.MemberDetailsResponse;
import com.jong.msa.board.common.constants.HeaderNames;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;
import com.jong.msa.board.common.enums.CodeEnum.Group;
import com.jong.msa.board.endpoint.user.mapper.UserRequestMapper;
import com.jong.msa.board.endpoint.user.request.UserLoginRequest;
import com.jong.msa.board.endpoint.user.swagger.UserAuthOperations;
import com.jong.msa.board.endpoint.user.swagger.UserAuthOperations.UserLoginOperation;
import com.jong.msa.board.endpoint.user.swagger.UserAuthOperations.UserLogoutOperation;
import com.jong.msa.board.endpoint.user.swagger.UserAuthOperations.UserRefreshOperation;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@UserAuthOperations
@RestController
@RequiredArgsConstructor
public class UserAuthRestController {

	private final Validator validator;
	
	private final UserRequestMapper requestMapper;
	
	private final MemberFeignClient memberFeignClient;
	
	private final RedisTemplate<String, String> redisTemplate;

	private final TokenProperties tokenProperties;

	private TokenProperties.Details accessTokenProperties;
	
	private TokenProperties.Details refreshTokenProperties;
	
	@PostConstruct
	private void init() {
		
		this.accessTokenProperties = tokenProperties.getAccessToken();
		this.refreshTokenProperties = tokenProperties.getRefreshToken();
	}
	
	@UserLoginOperation
	@PostMapping(value = "/apis/users/login",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> loginUser(
			@RequestBody UserLoginRequest request) {

		MemberLoginRequest loginRequest = requestMapper.toRequest(request);

		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(loginRequest, validator);
		
		MemberDetailsResponse member = memberFeignClient.loginMember(loginRequest).getBody();

		if (member.getGroup() != Group.USER) {
			throw new RestServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_USER_GROUP_MEMBER);
		} else {

			String accessToken = TokenUtils.generateToken(member.getId(), member.getGroup(), 
					accessTokenProperties.getSecretKey(), 
					accessTokenProperties.getExpireSeconds(), redisTemplate);
			
			String refreshToken = TokenUtils.generateToken(member.getId(), null, 
					refreshTokenProperties.getSecretKey(), 
					refreshTokenProperties.getExpireSeconds(), redisTemplate);
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.header(HeaderNames.ACCESS_TOKEN, accessToken)
					.header(HeaderNames.REFRESH_TOKEN, refreshToken)
					.build();
		}
	}

	@UserLogoutOperation
	@PostMapping(value = "/apis/users/logout")
	ResponseEntity<Void> logoutUser(
			@RequestHeader(name = HeaderNames.ACCESS_TOKEN) String accessToken) {

		TokenUtils.revokeToken(accessToken, redisTemplate);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@UserRefreshOperation
	@PostMapping(value = "/apis/users/refresh")
	ResponseEntity<Void> refreshUser(
			@RequestHeader(name = HeaderNames.REFRESH_TOKEN) String refreshToken) {
		
		try {
			
			UUID refreshMemberId = TokenUtils.validateToken(refreshToken, 
					refreshTokenProperties.getSecretKey(), redisTemplate, id -> id);

			MemberDetailsResponse member = memberFeignClient.getMember(refreshMemberId).getBody();
			
			if (member.getGroup() == Group.USER) {

				String newAccessToken = TokenUtils.generateToken(member.getId(), member.getGroup(), 
						accessTokenProperties.getSecretKey(), 
						accessTokenProperties.getExpireSeconds(), redisTemplate);
				
				String newRefreshToken = TokenUtils.generateToken(member.getId(), null, 
						refreshTokenProperties.getSecretKey(), 
						refreshTokenProperties.getExpireSeconds(), redisTemplate);

				TokenUtils.revokeToken(refreshToken, redisTemplate);

				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.header(HeaderNames.ACCESS_TOKEN, newAccessToken)
						.header(HeaderNames.REFRESH_TOKEN, newRefreshToken)
						.build();

			} else {
				throw new RestServiceException(HttpStatus.UNAUTHORIZED, ErrorCode.NOT_USER_GROUP_MEMBER_REFRESH_TOKEN);
			}
		} catch (ExpiredJwtException e) {
			throw new RestServiceException(HttpStatus.UNAUTHORIZED, ErrorCode.EXPIRED_REFRESH_TOKEN);
		} catch (RevokedJwtException e) {
			throw new RestServiceException(HttpStatus.UNAUTHORIZED, ErrorCode.REVOKED_REFRESH_TOKEN);
		} catch (JwtException e) {
			throw new RestServiceException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_REFRESH_TOKEN);
		}
	}

}
