package org.ab;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import org.ab.ast.SystemObject;
import org.ab.ast.parser.Parser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Git {
	private String repoPath;
	private String gitPrefix;
	private Parser parser;
	
	public Git(String repoPath) {
		this.repoPath = FilenameUtils.normalizeNoEndSeparator(repoPath);
		this.gitPrefix = "git --git-dir=" + this.repoPath + "/.git" ;
		this.parser = new Parser(repoPath);
	}
	
	public void checkout(String sha, String[] dirsToAnalyze) throws IOException {
		actuallyCheckout(sha);
		
		for (int i=0;i<dirsToAnalyze.length;i++) {
			Collection<File> filesInDirectory = FileUtils.listFiles(new File(repoPath + '/' + dirsToAnalyze[i]), new String[]{"java"}, true);
			for (File file : filesInDirectory) {
				SystemObject.getInstance().addFile(parser.parseFile(file));
			}
		}
	}
	
	private void actuallyCheckout(String sha) {
		String command = gitPrefix + " --work-tree=" + repoPath + " checkout -f " + sha;
		
		try {
			Process cmdProc = Runtime.getRuntime().exec(command);
			
			String line;
			BufferedReader stderrReader = new BufferedReader(
			         new InputStreamReader(cmdProc.getErrorStream()));
			while ((line = stderrReader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
