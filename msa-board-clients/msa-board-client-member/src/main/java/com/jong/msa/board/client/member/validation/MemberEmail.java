package com.jong.msa.board.client.member.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.jong.msa.board.client.core.validator.AbstractAnnotationValidator;
import com.jong.msa.board.common.utils.StringUtils;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MemberEmail.Validator.class)
public @interface MemberEmail {

	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	boolean nullable();
	
	String notBlankMessage();

	String overLengthMessage();

	String notFormatMessage();

	public static class Validator extends AbstractAnnotationValidator<MemberEmail, String> {

		public static final String FORMAT = "^[0-9a-zA-Z-_.]+@[0-9a-zA-Z-_.]+.[a-zA-Z]{2,3}$";
		
		@Override
		public void initialize(MemberEmail constraintAnnotation) {
			
			this.nullable = constraintAnnotation.nullable();
			this.validationMap.put(x -> StringUtils.isBlank(x), constraintAnnotation.notBlankMessage());
			this.validationMap.put(x -> StringUtils.isOverLength(x, 30), constraintAnnotation.overLengthMessage());
			this.validationMap.put(x -> StringUtils.isNotMatched(x, FORMAT), constraintAnnotation.notFormatMessage());
		}

	}
	
}
