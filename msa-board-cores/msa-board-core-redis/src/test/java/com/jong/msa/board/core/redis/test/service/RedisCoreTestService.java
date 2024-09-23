package com.jong.msa.board.core.redis.test.service;

import org.springframework.stereotype.Service;

import com.jong.msa.board.core.redis.annotation.RedisCaching;
import com.jong.msa.board.core.redis.annotation.RedisRemove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCoreTestService {

	public static final String CACHING_PREFIX = "test::";
	
	@RedisCaching(prefix = CACHING_PREFIX, key = "#key")
	public Object caching(String key, Object value) {
		
		log.info("Caching Method Parameter : key={}, value={}", key, value);
		
		return value;
	}
	
	@RedisRemove(prefix = CACHING_PREFIX, key = "#key")
	public void remove(String key) {

		log.info("Remove Method Parameter : key={}", key);
	}
	
}
