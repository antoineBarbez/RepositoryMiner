package org.ab;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;
import org.ab.mfb.FeatureEnvyMetricFileBuilder;
import org.ab.mfb.GodClassMetricFileBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

public class RepositoryMiner {
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			throw new IllegalArgumentException("Illegal Arguments.");
		}
		
		if (args.length == 3) {
			mineFromCommit(args[0], args[1], new String[]{""}, args[2]);
		}
		
		if (args.length == 4) {
			mineFromCommit(args[0], args[1], args[2].split("@", -1), args[3]);
		}
	}
	
	private static void mineFromCommit(String projectDir, String sha, String[] dirsToAnalyze, String outputDir) throws Exception {
		String normalizedOutputDir = FilenameUtils.normalizeNoEndSeparator(outputDir);
		
		try (Git git = openRepository(projectDir)) {
			MetricsExtractor metricsExtractor = new MetricsExtractor(git, new GodClassMetricFileBuilder());
			metricsExtractor.extractFromCommit(sha, dirsToAnalyze, normalizedOutputDir);
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
