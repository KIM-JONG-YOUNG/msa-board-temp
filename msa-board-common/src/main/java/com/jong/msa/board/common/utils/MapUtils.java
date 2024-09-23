package com.jong.msa.board.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public final class MapUtils {

	public static <K, V> Map<K, V> convertTo(K[] keys, V[] values) {

		Map<K, V> hashMap = new HashMap<>();

		IntStream.range(0, keys.length).forEach(i -> {
			hashMap.put(keys[i], values.length > i ? values[i] : null);
		});
		
		return hashMap;
	}
	
}
