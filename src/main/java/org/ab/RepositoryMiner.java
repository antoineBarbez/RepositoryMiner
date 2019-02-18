package org.ab;

import java.io.FileNotFoundException;

import org.ab.ast.SystemObject;
import org.ab.ast.visitors.SystemBuilder;
import org.ab.mfb.GodClassMetricFileBuilder;

public class RepositoryMiner {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new IllegalArgumentException("Illegal Arguments.");
		}
		
		mine(args[0], args[1]);
	}
	
	private static void mine(String repoFolder, String sha) throws FileNotFoundException {
		SystemBuilder builder = new SystemBuilder(repoFolder);
		builder.builtSystem(new String[]{"v4"});
		
		GodClassMetricFileBuilder fileBuilder = new GodClassMetricFileBuilder();
		fileBuilder.buildMetricFile("/Users/antoinebarbez/Desktop/metricFile.csv");
	}
}
