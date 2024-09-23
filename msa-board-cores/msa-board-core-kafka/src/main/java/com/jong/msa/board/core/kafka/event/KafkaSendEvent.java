package com.jong.msa.board.core.kafka.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.jong.msa.board.common.utils.FileUtils;
import com.jong.msa.board.common.utils.StringUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@RequiredArgsConstructor
public class KafkaSendEvent {

	private final String topic;
	
	private final String message;
	
	@Slf4j
	@Component
	@RequiredArgsConstructor
	public static class Listener {
		
		private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

		private final KafkaTemplate<String, String> kafkaTemplate;

		@Value("${spring.kafka.properties.error.log.path:./kafka-error}")
		private String errorLogPath;

		@EventListener
		@Async("threadPoolTaskExecutor")
		public void listen(KafkaSendEvent event) {
			
			try {
			
				kafkaTemplate.send(event.getTopic(), event.getMessage()).addCallback(
						result -> 
							log.info("Kafka Producer Send : topic={}, message={}", event.getTopic(), event.getMessage()),
						exception -> {
							
							log.error(exception.getMessage(), exception);

							FileUtils.create(
									StringUtils.concat(errorLogPath, "/producer-", formatter.format(LocalDateTime.now()), ".log"), 
									StringUtils.concat("- topic   : ", event.getTopic()), 
									StringUtils.concat("- message : ", event.getMessage()));
						});

			} catch (Exception e) {

				log.error(e.getMessage(), e);

				FileUtils.create(
						StringUtils.concat(errorLogPath, "/producer-", formatter.format(LocalDateTime.now()), ".log"), 
						StringUtils.concat("- topic   : ", event.getTopic()), 
						StringUtils.concat("- message : ", event.getMessage()));
			}
		}
		
	}
	
}
