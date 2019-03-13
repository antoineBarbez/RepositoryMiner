package org.ab;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

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
		String normalizedOutputDir = FilenameUtils.normalizeNoEndSeparator(outputDir);
		
		try (Repository repository = openRepository(projectDir)) {
			MetricsExtractor metricsExtractor = new MetricsExtractor(repository);
			metricsExtractor.extractFromCommit(sha, dirsToAnalyze, normalizedOutputDir);
		}
	}
	
	public static Repository openRepository(String projectDir) throws Exception {
	    File folder = new File(projectDir);
	    Repository repository;
	    if (folder.exists()) {
	        RepositoryBuilder builder = new RepositoryBuilder();
	        repository = builder
	            .setGitDir(new File(folder, ".git"))
	            .readEnvironment()
	            .findGitDir()
	            .build();
	    } else {
	        throw new FileNotFoundException(projectDir);
	    }
	    return repository;
	}
}
