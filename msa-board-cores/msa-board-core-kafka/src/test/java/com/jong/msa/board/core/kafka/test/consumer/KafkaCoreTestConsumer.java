package com.jong.msa.board.core.kafka.test.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaCoreTestConsumer {

	public static final String TEST_TOPIC = "test-topic";

	public static final String ERROR_TOPIC = "error-topic";

	public static final String GROUP_ID = "test-application";
	
	@KafkaListener(topics = TEST_TOPIC, groupId = GROUP_ID)
	public void consumeTestTopic(String message) {
		
		log.info("Kafka Consume Message Info : topic={}, message={}", TEST_TOPIC, message);
	}

	@KafkaListener(topics = ERROR_TOPIC, groupId = GROUP_ID)
	public void consumeErrorTopic(String message) {

		log.info("Kafka Consume Message Info : topic={}, message={}", ERROR_TOPIC, message);

		throw new RuntimeException("Kafka Error Topic Exception!!!");
	}

}
