package com.jong.msa.board.core.kafka.config;

import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.jong.msa.board.core.kafka.handler.KafkaListenerExceptionHandler;

@EnableKafka
@EnableAsync
@Configuration
@EnableConfigurationProperties({ KafkaProperties.class, TaskExecutionProperties.class })
public class KafkaCoreConfig {

	@Bean
	ProducerFactory<String, String> producerFactory(KafkaProperties properties) {

		Map<String, Object> propMap = properties.buildProducerProperties();
		propMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		propMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		propMap.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

		return new DefaultKafkaProducerFactory<>(propMap);
	}

	@Bean
	ConsumerFactory<String, String> consumerFactory(KafkaProperties properties) {

		Map<String, Object> propMap = properties.buildConsumerProperties();
		propMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		propMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		propMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		
		return new DefaultKafkaConsumerFactory<>(propMap);
	}

	@Bean
	KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {

		return new KafkaTemplate<>(producerFactory);
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
			ConsumerFactory<String, String> consumerFactory, KafkaListenerExceptionHandler kafkaListenerExceptionHandler) {

		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		factory.setCommonErrorHandler(kafkaListenerExceptionHandler);
		factory.getContainerProperties().setAckMode(AckMode.RECORD);

		return factory;
	}
	
	@Bean("threadPoolTaskExecutor")
	Executor threadPoolTaskExecutor(TaskExecutionProperties properties) {
		
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(properties.getPool().getCoreSize());
		threadPoolTaskExecutor.setMaxPoolSize(properties.getPool().getMaxSize());
		threadPoolTaskExecutor.setQueueCapacity(properties.getPool().getQueueCapacity());
	    
		return threadPoolTaskExecutor;
	}
	
}
