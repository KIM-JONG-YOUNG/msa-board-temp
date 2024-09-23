package com.jong.msa.board.common.utils;

import java.util.EnumSet;

import com.jong.msa.board.common.enums.CodeEnum;

public final class EnumUtils {

	public static <E extends Enum<E>> E converToEnum(String name, Class<E> enumType) {

		return EnumSet.allOf(enumType).stream()
				.filter(x -> x.name().equalsIgnoreCase(name))
				.findAny().orElseThrow(() -> new EnumConstantNotPresentException(enumType, name));
	}

	public static <E extends Enum<E> & CodeEnum<V>, V> E converToCodeEnum(V code, Class<E> enumType) {

		return EnumSet.allOf(enumType).stream()
				.filter(x -> x.getCode().equals(code))
				.findAny().orElseThrow(() -> new EnumConstantNotPresentException(enumType, (String) code));
	}

}
