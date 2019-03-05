package org.ab;

import org.ab.mfb.FeatureEnvyMetricFileBuilder;
import org.ab.mfb.GodClassMetricFileBuilder;
import org.apache.commons.io.FilenameUtils;

public class RepositoryMiner {
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			throw new IllegalArgumentException("Illegal Arguments.");
		}
		
		if (args.length == 3) {
			mine(args[0], args[1], new String[]{""}, args[2]);
		}
		
		if (args.length == 4) {
			mine(args[0], args[1], args[2].split("@", -1), args[3]);
		}
	}
	
	private static void mine(String repoFolder, String sha, String[] dirsToAnalyze, String outputDir) throws Exception {
		Git git = new Git(repoFolder);
		git.checkout(sha, dirsToAnalyze);
		
		String normOutputDir = FilenameUtils.normalizeNoEndSeparator(outputDir);
		
		GodClassMetricFileBuilder classFileBuilder = new GodClassMetricFileBuilder();
		classFileBuilder.buildMetricFile(normOutputDir + "/class_metrics.csv");
		
		FeatureEnvyMetricFileBuilder methodFileBuilder = new FeatureEnvyMetricFileBuilder();
		methodFileBuilder.buildMetricFile(normOutputDir + "/method_metrics.csv");
	}
}
