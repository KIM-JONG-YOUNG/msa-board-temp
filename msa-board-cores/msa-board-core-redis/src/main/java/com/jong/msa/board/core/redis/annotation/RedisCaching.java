package com.jong.msa.board.core.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.common.utils.MapUtils;
import com.jong.msa.board.common.utils.StringUtils;

import lombok.RequiredArgsConstructor;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCaching {

	String prefix();

	String key();

	long time() default 60;

	@Aspect
	@Component
	@RequiredArgsConstructor
	public static class Advisor {

		private final ObjectMapper objectMapper;
		
		private final RedisTemplate<String, String> redisTemplate;
		
		@Around("@annotation(com.jong.msa.board.core.redis.annotation.RedisCaching)")
		public Object processAround(ProceedingJoinPoint joinPoint) throws Throwable {

			MethodSignature signature = (MethodSignature) joinPoint.getSignature();

			Class<?> returnType = signature.getReturnType();

			if (returnType == void.class || returnType == Void.class) {
				return joinPoint.proceed();
			} else {
				
				RedisCaching annotation = signature.getMethod().getAnnotation(RedisCaching.class);

				StandardEvaluationContext context = new StandardEvaluationContext();
				SpelExpressionParser parser = new SpelExpressionParser();

				context.setVariables(MapUtils.convertTo(signature.getParameterNames(), joinPoint.getArgs()));
				
				Object parseValue = parser.parseExpression(annotation.key()).getValue(context);
				String cachingKey = StringUtils.concat(annotation.prefix(), parseValue); 
				
				try {

					String cachingVal = redisTemplate.opsForValue().get(cachingKey);

					return objectMapper.readValue(cachingVal, returnType);

				} catch (Exception e) {
					
					Object result = joinPoint.proceed();
					
					String cachingVal = objectMapper.writeValueAsString(result);
					Duration cachingTime = Duration.ofSeconds(annotation.time());

					redisTemplate.opsForValue().set(cachingKey, cachingVal, cachingTime);
					
					return result;
				}
			}
		}
		
	}
	
}
