package com.jong.msa.board.core.kafka.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import com.jong.msa.board.common.utils.FileUtils;
import com.jong.msa.board.common.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaListenerExceptionHandler implements CommonErrorHandler {

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
	
	@Value("${spring.kafka.properties.error.log.path:./kafka-error}")
	private String errorLogPath;
	
	@Override
	public void handleRecord(Exception thrownException, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer,
			MessageListenerContainer container) {
		
		log.error(thrownException.getMessage(), thrownException);

		FileUtils.create(
				StringUtils.concat(errorLogPath, "/consumer-", formatter.format(LocalDateTime.now()), ".log"),
				StringUtils.concat("- topic       : ", record.topic()),
				StringUtils.concat("- message     : ", record.value()));
	}

}
