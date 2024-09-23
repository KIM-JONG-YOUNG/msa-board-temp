package com.jong.msa.board.client.post.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.springframework.util.StringUtils;

import com.jong.msa.board.client.core.validator.AbstractAnnotationValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PostTitle.Validator.class)
public @interface PostTitle {

	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	boolean nullable();
	
	String notBlankMessage();

	String overLengthMessage();

	public static class Validator extends AbstractAnnotationValidator<PostTitle, String> {

		@Override
		public void initialize(PostTitle constraintAnnotation) {
			
			this.nullable = constraintAnnotation.nullable();
			this.validationMap.put(x -> !StringUtils.hasText(x), constraintAnnotation.notBlankMessage());
			this.validationMap.put(x -> x.length() > 300, constraintAnnotation.overLengthMessage());
		}

	}
	
}
