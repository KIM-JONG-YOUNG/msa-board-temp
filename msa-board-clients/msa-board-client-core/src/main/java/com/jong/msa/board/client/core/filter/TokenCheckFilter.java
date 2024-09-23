package com.jong.msa.board.client.core.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jong.msa.board.client.core.exception.RevokedJwtException;
import com.jong.msa.board.client.core.properties.TokenProperties;
import com.jong.msa.board.client.core.utils.TokenUtils;
import com.jong.msa.board.common.constants.HeaderNames;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;
import com.jong.msa.board.common.utils.StringUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnClass(SecurityContextConfigurer.class)
public class TokenCheckFilter extends OncePerRequestFilter {

	public static final String TOKEN_ERROR_CODE_ATTR_NAME = "token-error-code";
	
	private final TokenProperties tokenProperties;

	private final RedisTemplate<String, String> redisTemplate;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String accessToken = request.getHeader(HeaderNames.ACCESS_TOKEN);

		if (!StringUtils.isBlank(accessToken)) {

			try {
				
				Authentication authentication = TokenUtils.validateToken(accessToken, tokenProperties.getAccessToken().getSecretKey(), redisTemplate, 
						(id, group) -> {
							
							GrantedAuthority authority = new SimpleGrantedAuthority(StringUtils.concat("ROLE_", group.name()));
									
							return UsernamePasswordAuthenticationToken.authenticated(id, null, Arrays.asList(authority));
						});
				
				SecurityContextHolder.getContext().setAuthentication(authentication);

			} catch (ExpiredJwtException e) {
				request.setAttribute(TOKEN_ERROR_CODE_ATTR_NAME, ErrorCode.EXPIRED_ACCESS_TOKEN);
			} catch (RevokedJwtException e) {
				request.setAttribute(TOKEN_ERROR_CODE_ATTR_NAME, ErrorCode.REVOKED_ACCESS_TOKEN);
			} catch (JwtException e) {
				request.setAttribute(TOKEN_ERROR_CODE_ATTR_NAME, ErrorCode.INVALID_ACCESS_TOKEN);
			}
		}
		
		filterChain.doFilter(request, response);
	}

}
