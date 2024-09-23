package com.jong.msa.board.microservice.post.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.common.constants.KafkaTopics;
import com.jong.msa.board.core.kafka.event.KafkaSendEvent;
import com.jong.msa.board.domain.post.entity.PostEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class PostSaveEvent {

	private final PostEntity entity;
	
	@Component
	@RequiredArgsConstructor
	public static class Listener {
		
		private final ObjectMapper objectMapper;
		
		private final ApplicationEventPublisher eventPublisher;
		
		@EventListener
		public void listen(PostSaveEvent event) throws Exception {
			
			String message = objectMapper.writeValueAsString(event.getEntity());

			eventPublisher.publishEvent(new KafkaSendEvent(KafkaTopics.POST_TOPIC, message));
		}
		
	}
	
}
