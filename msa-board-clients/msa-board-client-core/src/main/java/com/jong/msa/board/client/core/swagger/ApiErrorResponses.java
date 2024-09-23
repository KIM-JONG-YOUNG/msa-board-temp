package com.jong.msa.board.client.core.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jong.msa.board.common.enums.CodeEnum.ErrorCode;

@Inherited
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponses {

	ApiErrorResponse[] value();

	@Inherited
	@Target(ElementType.ANNOTATION_TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Repeatable(ApiErrorResponses.class)
	public @interface ApiErrorResponse {

		String responseCode();
		
		ErrorCode errorCode();

	}

}
