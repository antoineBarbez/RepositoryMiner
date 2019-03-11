package org.ab;

import java.io.File;
import java.util.Collection;

import org.ab.ast.SystemObject;
import org.ab.ast.parser.Parser;
import org.ab.mfb.FeatureEnvyMetricFileBuilder;
import org.ab.mfb.GodClassMetricFileBuilder;
import org.ab.utils.GitUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Repository;

public class MetricsExtractor {
	
	public void extractAtCommit(String projectDir, String sha, String[] dirs, String outputDir) throws Exception {
		Repository repository = GitUtils.openRepository(projectDir);
		GitUtils.checkout(repository, sha);
		
		Parser parser = new Parser(projectDir);
		for (int i=0;i<dirs.length;i++) {
			Collection<File> filesInDirectory = FileUtils.listFiles(new File(projectDir + '/' + dirs[i]), new String[]{"java"}, true);
			for (File file : filesInDirectory) {
				SystemObject.getInstance().addFile(parser.parseFile(file));
			}
		}
		
		GodClassMetricFileBuilder classFileBuilder = new GodClassMetricFileBuilder();
		classFileBuilder.buildMetricFile(outputDir + "/class_metrics.csv");
		
		FeatureEnvyMetricFileBuilder methodFileBuilder = new FeatureEnvyMetricFileBuilder();
		methodFileBuilder.buildMetricFile(outputDir + "/method_metrics.csv");
	}
}
