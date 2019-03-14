package org.ab.metrics;

import java.io.File;
import java.util.Collection;

import org.ab.RepositoryMiner;
import org.ab.ast.SystemObject;
import org.ab.ast.parser.Parser;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//JUnit Suite Test
@RunWith(Suite.class)

@Suite.SuiteClasses({ 
   CYCLOTest.class,
   LOCTest.class
})

public class AllMetricTests {
	
	@BeforeClass
    public static void setUp() throws Exception {
		String projectDir = "/Users/antoinebarbez/Desktop/TEMP/android-platform-support";
		String sha = "38fc0cf9d7e38258009f1a053d35827e24563de6";
		String[] dirs = new String[] {"v4"};
		
		try (Git git = RepositoryMiner.openRepository(projectDir)) {
			git.checkout().setName(sha).call();
			
			Parser parser = new Parser(projectDir);
			for (int i=0;i<dirs.length;i++) {
				Collection<File> filesInDirectory = FileUtils.listFiles(new File(projectDir + '/' + dirs[i]), new String[]{"java"}, true);
				for (File file : filesInDirectory) {
					SystemObject.getInstance().addFile(parser.parseFile(file));
				}
			}
		}
    }	
}
