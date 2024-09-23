package com.jong.msa.board.client.core.handler;

import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jong.msa.board.client.core.exception.RestServiceException;
import com.jong.msa.board.client.core.response.ErrorResponse;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class RestServiceExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(RestServiceException.class)
	public ResponseEntity<ErrorResponse> handleRestServiceException(RestServiceException e) {

		return ResponseEntity.status(e.getStatus())
				.body(ErrorResponse.builder()
						.errorCode(e.getErrorCode())
						.errorDetailsList(e.getErrorList().stream()
								.map(x -> x instanceof FieldError 
										? ErrorResponse.Details.builder()
												.field(((FieldError) x).getField())
												.message(x.getDefaultMessage())
												.build()
										: ErrorResponse.Details.builder()
												.message(x.getDefaultMessage())
												.build())
								.collect(Collectors.toList()))
						.build());
	}
	
	@ExceptionHandler(FeignException.class)
	public ResponseEntity<ErrorResponse> handleFeignException(FeignException e) {
		
		log.error(e.getMessage(), e);
		
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
				.body(ErrorResponse.builder()
						.errorCode(ErrorCode.UNCHECKED_EXTERNAL_ERROR)
						.build());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {

		log.error(e.getMessage(), e);
	
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ErrorResponse.builder()
						.errorCode(ErrorCode.UNCHECKED_INTERNAL_ERROR)
						.build());
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		log.error(ex.getMessage(), ex);

		return ResponseEntity.status(status)
				.body(ErrorResponse.builder()
						.errorCode(ErrorCode.UNCHECKED_INTERNAL_ERROR)
						.build());
	}
	
}
