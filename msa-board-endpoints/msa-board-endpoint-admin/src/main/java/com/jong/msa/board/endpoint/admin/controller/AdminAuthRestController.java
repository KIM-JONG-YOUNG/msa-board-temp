package com.jong.msa.board.endpoint.admin.controller;

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
import com.jong.msa.board.endpoint.admin.mapper.AdminRequestMapper;
import com.jong.msa.board.endpoint.admin.request.AdminLoginRequest;
import com.jong.msa.board.endpoint.admin.swagger.AdminAuthOperations;
import com.jong.msa.board.endpoint.admin.swagger.AdminAuthOperations.AdminLoginOperation;
import com.jong.msa.board.endpoint.admin.swagger.AdminAuthOperations.AdminLogoutOperation;
import com.jong.msa.board.endpoint.admin.swagger.AdminAuthOperations.AdminRefreshOperation;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@AdminAuthOperations
@RestController
@RequiredArgsConstructor
public class AdminAuthRestController {

	private final Validator validator;
	
	private final AdminRequestMapper requestMapper;
	
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
	
	@AdminLoginOperation
	@PostMapping(value = "/apis/admins/login",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> loginAdmin(
			@RequestBody AdminLoginRequest request) {
		
		MemberLoginRequest loginRequest = requestMapper.toRequest(request);

		ValidationUtils.validateBy(request, validator);
		ValidationUtils.validateBy(loginRequest, validator);
		
		MemberDetailsResponse member = memberFeignClient.loginMember(loginRequest).getBody();

		if (member.getGroup() != Group.ADMIN) {
			throw new RestServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_ADMIN_GROUP_MEMBER_USERNAME);
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

	@AdminLogoutOperation
	@PostMapping(value = "/apis/admins/logout")
	ResponseEntity<Void> logoutAdmin(
			@RequestHeader(name = HeaderNames.ACCESS_TOKEN) String accessToken) {
		
		TokenUtils.revokeToken(accessToken, redisTemplate);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@AdminRefreshOperation
	@PostMapping(value = "/apis/admins/refresh")
	ResponseEntity<Void> refreshAdmin(
			@RequestHeader(name = HeaderNames.REFRESH_TOKEN) String refreshToken) {
		
		try {
			
			UUID refreshMemberId = TokenUtils.validateToken(refreshToken, 
					refreshTokenProperties.getSecretKey(), redisTemplate, id -> id);

			MemberDetailsResponse member = memberFeignClient.getMember(refreshMemberId).getBody();
			
			if (member.getGroup() == Group.ADMIN) {

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
				throw new RestServiceException(HttpStatus.UNAUTHORIZED, ErrorCode.NOT_ADMIN_GROUP_MEMBER_REFRESH_TOKEN);
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
