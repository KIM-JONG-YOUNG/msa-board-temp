package com.jong.msa.board.core.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.jong.msa.board.common.utils.StringUtils;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(RedisProperties.class)
public class RedisCoreConfig {

	@Bean 
	RedisConnectionFactory redisConnectionFactory(RedisProperties properties) {
		
		return new LettuceConnectionFactory(new RedisStandaloneConfiguration(
				properties.getHost(), properties.getPort()));
	}

	@Bean
	RedissonClient redissonClient(RedisProperties properties) {
		
		String redisURL = StringUtils.concat("redis://", properties.getHost(), ":", properties.getPort());
	
		org.redisson.config.Config config = new Config();

		config.useSingleServer().setAddress(redisURL);
		
		return Redisson.create(config);
	}

	@Bean
	RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());

		return redisTemplate;
	}
	
}
