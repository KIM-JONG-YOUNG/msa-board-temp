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
@Constraint(validatedBy = MemberPassword.Validator.class)
public @interface MemberPassword {

	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	boolean nullable();
	
	String notBlankMessage();

	String underLengthMessage();

	public static class Validator extends AbstractAnnotationValidator<MemberPassword, String> {

		@Override
		public void initialize(MemberPassword constraintAnnotation) {
			
			this.nullable = constraintAnnotation.nullable();
			this.validationMap.put(x -> StringUtils.isBlank(x), constraintAnnotation.notBlankMessage());
			this.validationMap.put(x -> StringUtils.isUnderLength(x, 8), constraintAnnotation.underLengthMessage());
		}

	}
	
}
