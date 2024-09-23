package com.jong.msa.board.client.core.utils;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.jong.msa.board.client.core.exception.RestServiceException;
import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;

public class ValidationUtils {

	public static void validateBy(Object request, Validator validator) {
		
		BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");

		validator.validate(request, bindingResult);
		
		if (bindingResult.hasErrors()) throw new RestServiceException(
				HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER, bindingResult.getAllErrors());
	}
	
}
