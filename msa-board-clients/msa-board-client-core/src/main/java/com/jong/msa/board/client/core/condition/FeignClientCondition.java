package com.jong.msa.board.client.core.condition;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class FeignClientCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

		MergedAnnotation<FeignClient> annotation = metadata.getAnnotations().get(FeignClient.class);

		String springApplicationName = context.getEnvironment().getProperty("spring.application.name");
		String feignClientName = annotation.getString("name");
		
		return !springApplicationName.equalsIgnoreCase(feignClientName);
	}

}
