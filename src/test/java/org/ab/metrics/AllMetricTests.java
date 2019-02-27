package org.ab.metrics;

import java.io.IOException;

import org.ab.Git;
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
    public static void setUp() throws IOException {
		String repoPath = "/Users/antoinebarbez/Desktop/TEMP/android-platform-support";
		String sha = "38fc0cf9d7e38258009f1a053d35827e24563de6";
		String[] dirsToAnalyze = new String[] {"v4"};
		
		Git git = new Git(repoPath);
		git.checkout(sha, dirsToAnalyze);
    }	
}
