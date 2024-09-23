package com.jong.msa.board.common.utils;

import java.util.Arrays;
import java.util.regex.Pattern;

public final class StringUtils {

	public static String concat(Object... objects) {
		
		StringBuilder stringBuilder = new StringBuilder();

		Arrays.stream(objects).filter(x -> x != null).forEach(stringBuilder::append);
		
		return stringBuilder.toString();
	}
	
	public static boolean isBlank(String string) {
		
		return string == null || string.trim().length() == 0;
	}

	public static boolean isOverLength(String string, int max) {
		
		return ((string == null) ? 0 : string.length()) > max;
	}

	public static boolean isUnderLength(String string, int min) {
		
		return ((string == null) ? 0 : string.length()) < min;
	}

	public static boolean isNotMatched(String string, String regex) {
		
		return (string == null) ? false : !Pattern.matches(regex, string);
	}

}
