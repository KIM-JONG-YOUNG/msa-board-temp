package com.jong.msa.board.core.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.jong.msa.board.common.utils.MapUtils;

import lombok.RequiredArgsConstructor;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisRemove {

	String prefix();

	String key();

	@Aspect
	@Component
	@RequiredArgsConstructor
	public static class Advisor {

		private final RedisTemplate<String, String> redisTemplate;
		
		@AfterReturning("@annotation(com.jong.msa.board.core.redis.annotation.RedisRemove)")
		public void afterReturning(JoinPoint joinPoint) throws Throwable {

			MethodSignature signature = (MethodSignature) joinPoint.getSignature();

			RedisRemove annotation = signature.getMethod().getAnnotation(RedisRemove.class);

			StandardEvaluationContext context = new StandardEvaluationContext();
			SpelExpressionParser parser = new SpelExpressionParser();

			context.setVariables(MapUtils.convertTo(signature.getParameterNames(), joinPoint.getArgs()));
			
			Object parseValue = parser.parseExpression(annotation.key()).getValue(context);
			String cachingKey = new StringBuilder(annotation.prefix()).append(parseValue).toString();

			redisTemplate.delete(cachingKey);
		}
		
	}
	
}
