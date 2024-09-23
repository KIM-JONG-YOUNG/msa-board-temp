package com.jong.msa.board.core.redis.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.common.constants.PackageNames;
import com.jong.msa.board.common.utils.StringUtils;
import com.jong.msa.board.core.redis.test.service.RedisCoreTestService;

import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

@SpringBootTest
public class RedisCoreTest {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@SpyBean
	RedisCoreTestService service;

	@SpringBootApplication(scanBasePackages = PackageNames.ROOT_PACKAGE)
	public static class Application {}
	
	@TestConfiguration
	public static class Config {
		
		private final RedisServer redisServer;

		public Config(@Value("${spring.redis.port}") int port) {
			
			redisServer = RedisServer.builder()
					.redisExecProvider(RedisExecProvider.defaultProvider()
							.override(OS.MAC_OS_X, Architecture.x86_64, "binary/redis-server-6.2.5-mac-arm64")
							.override(OS.MAC_OS_X, Architecture.x86, "binary/redis-server-6.2.5-mac-arm64"))
					.port(port)
					.build();
		}
		
		@PostConstruct
		void startRedisServer() {
			redisServer.start();
		}

		@PreDestroy
		void stopRedisServer() {
			redisServer.stop();
		}

		@Bean
		ObjectMapper objectMapper() {
			return new ObjectMapper();
		}
		
	}
	
	@Test
	void RedisCaching_어노테이션_테스트() throws Exception {
		
		String key = "caching-key";
		
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("prop-1", "value-1");
		valueMap.put("prop-2", "value-2");
		
		service.caching(key, valueMap);
		service.caching(key, valueMap);

		verify(service, timeout(5000)).caching(key, valueMap);

		String cachingKey = StringUtils.concat(RedisCoreTestService.CACHING_PREFIX, key);
		String cachingVal = redisTemplate.opsForValue().get(cachingKey);
		
		Map<String, Object> cachingValueMap = 
				objectMapper.readValue(cachingVal, new TypeReference<Map<String, Object>>() {});

		assertEquals(valueMap.get("prop-1"), cachingValueMap.get("prop-1"));
		assertEquals(valueMap.get("prop-2"), cachingValueMap.get("prop-2"));
	}

	@Test
	void RedisRemove_어노테이션_테스트() {
		
		String key = "remove-key";
		
		String cachingKey = StringUtils.concat(RedisCoreTestService.CACHING_PREFIX, key);
		String cachingVal = "caching-value";
		
		redisTemplate.opsForValue().set(cachingKey, cachingVal, Duration.ofSeconds(10));
		
		service.remove(key);
		
		assertFalse(redisTemplate.hasKey(cachingKey));
	}

}
