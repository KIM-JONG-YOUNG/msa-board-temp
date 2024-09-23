package com.jong.msa.board.core.kafka.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;

import com.jong.msa.board.common.constants.PackageNames;
import com.jong.msa.board.core.kafka.event.KafkaSendEvent;
import com.jong.msa.board.core.kafka.handler.KafkaListenerExceptionHandler;
import com.jong.msa.board.core.kafka.test.consumer.KafkaCoreTestConsumer;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@EmbeddedKafka(
		count = 1, partitions = 1, ports = 9092,
		brokerProperties = "auto.create.topics.enable=false")
public class KafkaCoreTest {

	@Autowired 
	EmbeddedKafkaBroker kafkaBroker;
	
	@Autowired
	ApplicationEventPublisher eventPublisher;
	
	@SpyBean
	KafkaSendEvent.Listener eventListener;
	
	@SpyBean
	KafkaCoreTestConsumer consumer;

	@SpyBean
	KafkaListenerExceptionHandler consumerErrorHandler;

	@SpringBootApplication(scanBasePackages = PackageNames.ROOT_PACKAGE)
	public static class Application {}

	@BeforeAll
	void addTopics() {
		
		kafkaBroker.addTopics(
				KafkaCoreTestConsumer.TEST_TOPIC,
				KafkaCoreTestConsumer.ERROR_TOPIC);
	}

	@Test
	void KafkaSendEvent_테스트() {
		
		KafkaSendEvent event = new KafkaSendEvent(KafkaCoreTestConsumer.TEST_TOPIC, "KafkaSendEvent_테스트");
		
		eventPublisher.publishEvent(event);
		
		verify(eventListener, timeout(5000)).listen(event);
		verify(consumer, timeout(5000)).consumeTestTopic(event.getMessage());
	}
	
	@Test
	void Kafka_존재하지_않는_토픽_전송_에러_테스트() {
		
		KafkaSendEvent event = new KafkaSendEvent("not-exists-topic", "Kafka_메시지_전송_에러_테스트");

		eventPublisher.publishEvent(event);
		
		verify(eventListener, timeout(5000)).listen(event);
	}

	@Test
	void Kafka_1MB_이상_메시지_처리_에러_테스트() throws Exception {

		int messageSize = 2 * 1024 * 1024;
		StringBuilder largeMessage = new StringBuilder(messageSize);
		
		IntStream.range(0, messageSize).forEach(i -> largeMessage.append("A"));
		
		KafkaSendEvent event = new KafkaSendEvent(KafkaCoreTestConsumer.TEST_TOPIC, largeMessage.toString());

		eventPublisher.publishEvent(event);
		
		verify(eventListener, timeout(5000)).listen(event);
	
		// 파일 생성 대기 
		Thread.sleep(3000);
	}
	
	@Test
	void KafkaListener_에러_테스트() throws Exception {

		KafkaSendEvent event = new KafkaSendEvent(KafkaCoreTestConsumer.ERROR_TOPIC, "KafkaListener_에러_테스트");

		eventPublisher.publishEvent(event);
		
		verify(eventListener, timeout(5000)).listen(event);
		verify(consumerErrorHandler, timeout(5000)).handleRecord(any(), any(), any(), any());
	}
	
}
