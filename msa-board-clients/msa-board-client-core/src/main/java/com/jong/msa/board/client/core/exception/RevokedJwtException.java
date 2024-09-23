package com.jong.msa.board.client.core.exception;

import io.jsonwebtoken.JwtException;

public class RevokedJwtException extends JwtException {

	private static final long serialVersionUID = 1L;

	public RevokedJwtException(String message) {
		super(message);
	}

}
