package com.jong.msa.board.client.core.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jong.msa.board.client.core.filter.TokenCheckFilter;
import com.jong.msa.board.client.core.response.ErrorResponse;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@ConditionalOnClass(SecurityContextConfigurer.class)
public class SecurityExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException e) {
		
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		ErrorCode errorCode = (ErrorCode) request.getAttribute(TokenCheckFilter.TOKEN_ERROR_CODE_ATTR_NAME);

		if (errorCode == null) {
			status = HttpStatus.FORBIDDEN;
			errorCode = ErrorCode.NOT_ACCESSABLE_URL;
		}
		
		return ResponseEntity.status(status)
				.body(ErrorResponse.builder()
						.errorCode(errorCode)
						.build());
	}
	
}
