package com.jong.msa.board.client.core.validator;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public abstract class AbstractAnnotationValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

	protected final Map<Predicate<T>, String> validationMap = new LinkedHashMap<>();
	
	protected boolean nullable;

	@Override
	public abstract void initialize(A constraintAnnotation);
	
	@Override
	public boolean isValid(T value, ConstraintValidatorContext context) {
		
		if (nullable && value == null) {
			return true;
		} else {

			for (Entry<Predicate<T>, String> validationEntry : validationMap.entrySet()) {
				
				Predicate<T> validationFunction = validationEntry.getKey();
				String validationMessage = validationEntry.getValue();
				
				if (validationFunction.test(value)) {

					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(validationMessage)
						   .addConstraintViolation();
					
					return false;
				}
			}
			
			return true;
		}
	}

}
