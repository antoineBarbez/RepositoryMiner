package org.ab;

import org.apache.commons.io.FilenameUtils;

public class RepositoryMiner {
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			throw new IllegalArgumentException("Illegal Arguments.");
		}
		
		if (args.length == 3) {
			mineAtCommit(args[0], args[1], new String[]{""}, args[2]);
		}
		
		if (args.length == 4) {
			mineAtCommit(args[0], args[1], args[2].split("@", -1), args[3]);
		}
	}
	
	private static void mineAtCommit(String projectDir, String sha, String[] dirsToAnalyze, String outputDir) throws Exception {
		String normalizedProjectDir = FilenameUtils.normalizeNoEndSeparator(projectDir);
		String normalizedOutputDir = FilenameUtils.normalizeNoEndSeparator(outputDir);
		
		MetricsExtractor metricsExtractor = new MetricsExtractor();
		metricsExtractor.extractAtCommit(normalizedProjectDir, sha, dirsToAnalyze, normalizedOutputDir);
	}
}
