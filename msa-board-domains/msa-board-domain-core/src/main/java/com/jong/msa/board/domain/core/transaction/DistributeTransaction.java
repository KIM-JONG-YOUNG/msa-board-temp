package com.jong.msa.board.domain.core.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisTimeoutException;
import org.redisson.client.RedisTryAgainException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.jong.msa.board.common.utils.MapUtils;
import com.jong.msa.board.common.utils.StringUtils;
import com.jong.msa.board.core.redis.config.RedisCoreConfig;

import lombok.RequiredArgsConstructor;

@Order(Ordered.LOWEST_PRECEDENCE)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeTransaction {

	String prefix();

	String key();

	long waitTime() default 5;

	long leaseTime() default 3;

	@Aspect
	@Component
	@RequiredArgsConstructor 
	@ConditionalOnClass(RedisCoreConfig.class)
	public static class Advisor {

		private final RedissonClient redissonClient;

		private final TransactionTemplate transactionTemplate;
		
		@Around("@annotation(com.jong.msa.board.domain.core.transaction.DistributeTransaction)")
		public Object processAround(ProceedingJoinPoint joinPoint) throws Throwable {

			MethodSignature signature = (MethodSignature) joinPoint.getSignature();

			DistributeTransaction annotation = signature.getMethod().getAnnotation(DistributeTransaction.class);

			StandardEvaluationContext context = new StandardEvaluationContext();
			SpelExpressionParser parser = new SpelExpressionParser();

			context.setVariables(MapUtils.convertTo(signature.getParameterNames(), joinPoint.getArgs()));
			
			Object parseValue = parser.parseExpression(annotation.key()).getValue(context);
			String lockName = StringUtils.concat(annotation.prefix(), parseValue);
			
			RLock redissonLock = redissonClient.getLock(lockName);

			if (redissonLock.tryLock(annotation.waitTime(), annotation.leaseTime(), TimeUnit.SECONDS)) {

				AtomicReference<Object> transactionResult = new AtomicReference<>();
				AtomicReference<Throwable> transactionException = new AtomicReference<>();

				transactionTemplate.executeWithoutResult(status -> {
					
					try {
						transactionResult.set(joinPoint.proceed());
					} catch (Throwable e) {
						transactionException.set(e);
					} finally {
						
						if (!redissonLock.isLocked()) transactionException.set(new RedisTimeoutException(
								StringUtils.concat("Redisson Lock 실행시간을 초과하였습니다. (lockName: ", lockName, ")")));
						
						if (transactionException.get() != null) status.setRollbackOnly(); 
					}
				});

				if (redissonLock.isLocked()) redissonLock.unlock();
				
				if (transactionException.get() != null) {
					throw transactionException.get();	
				} else {
					return transactionResult.get();
				}
				
			} else {
				throw new RedisTryAgainException(
						StringUtils.concat("Redisson Lock 대기시간을 초과하였습니다. (lockName: ", lockName, ")"));
			}
		}
		
	}

}
