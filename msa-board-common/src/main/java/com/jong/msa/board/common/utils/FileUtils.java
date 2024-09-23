package com.jong.msa.board.common.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class FileUtils {

	public static void create(String path, String... lines) {
		
		File file = new File(path);
		
		file.getParentFile().mkdirs();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
		
			writer.write(Arrays.stream(lines).collect(Collectors.joining("\n")));
			writer.flush();

		} catch (Exception e) {
			
			throw new RuntimeException(e);
		}
	}

}
