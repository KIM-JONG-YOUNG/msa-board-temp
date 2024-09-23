package com.jong.msa.board.client.core.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RestServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final HttpStatus status;
	
	private final ErrorCode errorCode;
	
	private final List<ObjectError> errorList;

	public RestServiceException(HttpStatus status, ErrorCode errorCode) {
		
		super(errorCode.getMessage());
		this.status = status;
		this.errorCode = errorCode;
		this.errorList = new ArrayList<>();
	}
 
	public RestServiceException(HttpStatus status, ErrorCode errorCode, List<ObjectError> errorList) {

		this(status, errorCode);
		this.errorList.addAll(errorList);
	}

}
