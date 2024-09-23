package com.jong.msa.board.client.core.utils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import com.jong.msa.board.client.core.exception.RevokedJwtException;
import com.jong.msa.board.common.constants.RedisPrefixes;
import com.jong.msa.board.common.enums.CodeEnum.Group;
import com.jong.msa.board.common.utils.EnumUtils;
import com.jong.msa.board.common.utils.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

public final class TokenUtils {
	
	private static final String MEMBER_ID_KEY = "member-id";
	private static final String MEMBER_GROUP_KEY = "member-group";

	public static String generateToken(UUID memberId, Group memberGroup, 
			String secretKey, long expireSeconds, 
			RedisTemplate<String, String> redisTemplate) {
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expireDateTime = now.plusSeconds(expireSeconds);

		String token = Jwts.builder()
				.setId(UUID.randomUUID().toString())
				.claim(MEMBER_ID_KEY, memberId.toString())
				.claim(MEMBER_GROUP_KEY, (memberGroup != null) ? memberGroup.name() : null)
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.setExpiration(Timestamp.valueOf(expireDateTime))
				.compact();
		
		String cachingKey = StringUtils.concat(RedisPrefixes.TOKEN_PREFIX, memberId, "::", token);
		Duration cachingTime = Duration.ofSeconds(expireSeconds);

		redisTemplate.opsForValue().set(cachingKey, "", cachingTime);

		return token;
	}
	

	public static void revokeToken(String token, RedisTemplate<String, String> redisTemplate) {

		String cachingKeyPattern = StringUtils.concat(RedisPrefixes.TOKEN_PREFIX, "*::", token);
		ScanOptions cachingKeyScanOptions = ScanOptions.scanOptions().match(cachingKeyPattern).count(1).build();
		Cursor<String> cachingKeyCursor = redisTemplate.scan(cachingKeyScanOptions);

	    while (cachingKeyCursor.hasNext()) {
	    	redisTemplate.delete(cachingKeyCursor.next());
	    	break;
	    }
	}
	
	public static void revokeTokenAll(UUID memberId, RedisTemplate<String, String> redisTemplate) {
		
		String cachingKeyPattern = StringUtils.concat(RedisPrefixes.TOKEN_PREFIX, memberId, "::*");
		ScanOptions cachingKeyScanOptions = ScanOptions.scanOptions().match(cachingKeyPattern).count(10).build();
		Cursor<String> cachingKeyCursor = redisTemplate.scan(cachingKeyScanOptions);

	    while (cachingKeyCursor.hasNext()) {
	    	redisTemplate.delete(cachingKeyCursor.next());
	    }
	}
	
	public static <T> T validateToken(String token, String secretKey, 
			RedisTemplate<String, String> redisTemplate, 
			Function<UUID, T> afterFunction) {

		UUID memberId = null;
		
		Claims claims = Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody();

		try {
			memberId = UUID.fromString(claims.get("id", String.class));
		} catch (Exception e) {
			throw new MalformedJwtException("Token에 ID 정보가 저장되어 있지 않습니다.");
		}

		String cachingKey = StringUtils.concat(RedisPrefixes.TOKEN_PREFIX, memberId, "::", token);

		if (redisTemplate.hasKey(cachingKey)) {
			return afterFunction.apply(memberId);
		} else {
			throw new RevokedJwtException("사용할 수 없는 Token입니다.");
		}
	}
	
	public static <T> T validateToken(String token, String secretKey, 
			RedisTemplate<String, String> redisTemplate, 
			BiFunction<UUID, Group, T> afterFunction) {

		UUID memberId = null;
		Group memberGroup = null;
		
		Claims claims = Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody();

		try {
			memberId = UUID.fromString(claims.get(MEMBER_ID_KEY, String.class));
			memberGroup = EnumUtils.converToEnum(claims.get(MEMBER_GROUP_KEY, String.class), Group.class);
		} catch (Exception e) {
			throw new MalformedJwtException("Token에 ID 혹은 Group 정보가 저장되어 있지 않습니다.");
		}

		String cachingKey = StringUtils.concat(RedisPrefixes.TOKEN_PREFIX, memberId, "::", token);

		if (redisTemplate.hasKey(cachingKey)) {
			return afterFunction.apply(memberId, memberGroup);
		} else {
			throw new RevokedJwtException("사용할 수 없는 Token입니다.");
		}
	}
	
}
