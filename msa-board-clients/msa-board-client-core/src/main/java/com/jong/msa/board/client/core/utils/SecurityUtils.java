package com.jong.msa.board.client.core.utils;

import java.util.UUID;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

	public static UUID getSessionId() {
		
		SecurityContext context = SecurityContextHolder.getContext();
		
		return UUID.fromString(context.getAuthentication().getPrincipal().toString());
	}
	
}
