package com.github.mauricioaniche.ck.plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Helper {

	/**
	 * Extract comma or semicolon-separated list of directores
	 * 
	 * @param srcDirs
	 * @return
	 */
	public static Collection<String> extractDirs(Collection<String> srcDirs ) {
		Set<String> outDirs = new LinkedHashSet<String>();
		for (String dir: srcDirs) {
			Set<String> parsedDirs = Arrays.stream(dir.split("[,;]")).map(s->s.trim()).collect(Collectors.toSet());
			outDirs.addAll(parsedDirs);
		}
		return outDirs;
	}

}
