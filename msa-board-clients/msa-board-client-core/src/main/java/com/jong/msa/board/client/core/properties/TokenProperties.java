package com.jong.msa.board.client.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties("jwt")
public class TokenProperties {

	private final Details accessToken;
	
	private final Details refreshToken;

	@Getter
	@ToString
	@ConstructorBinding
	@RequiredArgsConstructor
	public static class Details {

		private final String secretKey;
	
		private final long expireSeconds;
	}
	
}
