package com.jong.msa.board.client.search.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.jong.msa.board.client.core.validator.AbstractAnnotationValidator;
import com.jong.msa.board.client.search.request.DateRange;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Constraint(validatedBy = BetweenDate.Validator.class)
public @interface BetweenDate {

	String message();

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

	public static class Validator extends AbstractAnnotationValidator<BetweenDate, DateRange> {

		@Override
		public void initialize(BetweenDate constraintAnnotation) {

			Predicate<DateRange> isExistsFrom = x -> x.getFrom() != null;
			Predicate<DateRange> isExistsTo = x -> x.getTo() != null;
			Predicate<DateRange> isBefore = x -> x.getTo().isBefore(x.getFrom());
			
			this.nullable = true;
			this.validationMap.put(isExistsFrom.and(isExistsTo.and(isBefore)), constraintAnnotation.message());
		}
		
	}
	
}
