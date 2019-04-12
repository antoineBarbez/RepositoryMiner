package org.ab;

import java.io.File;
import java.io.FileNotFoundException;

import org.ab.mfb.IMetricFileBuilder;
import org.ab.mfb.impl.FeatureEnvyMetricFileBuilder;
import org.ab.mfb.impl.GodClassMetricFileBuilder;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

public class RepositoryMiner {
	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
			throw new IllegalArgumentException("Illegal Arguments.");
		}
		
		final String option = args[0];
		if (!(option.equalsIgnoreCase("-gc") || option.equalsIgnoreCase("-fe"))) {
			throw new IllegalArgumentException("Illegal Arguments.");
		}
		
		if (args.length == 4) {
			mineFromCommit(option, args[1], args[2], new String[]{""}, args[3]);
		}
		
		if (args.length == 5) {
			mineFromCommit(option, args[1], args[2], args[3].split("@", -1), args[4]);
		}
	}
	
	private static void mineFromCommit(String option, String projectDir, String sha, String[] dirsToAnalyze, String outputDir) throws Exception {
		String normalizedOutputDir = FilenameUtils.normalizeNoEndSeparator(outputDir);
		IMetricFileBuilder mfb;
		if (option.equalsIgnoreCase("-gc")) {
			mfb = new GodClassMetricFileBuilder();
		}else {
			mfb = new FeatureEnvyMetricFileBuilder();
		}
		
		try (Git git = openRepository(projectDir)) {
			MetricsExtractor metricsExtractor = new MetricsExtractor(git, mfb);
			metricsExtractor.extractFromCommit(sha, dirsToAnalyze, normalizedOutputDir, 1000);
		}
	}
	
	public static Git openRepository(String projectDir) throws Exception {
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
	    return new Git(repository);
	}
}
