package org.ab;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.ab.ast.FileObject;
import org.ab.ast.SystemObject;
import org.ab.ast.parser.Parser;
import org.ab.mfb.IMetricFileBuilder;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

public class MetricsExtractor {
	private Git git;
	private IMetricFileBuilder mfb;
	private Parser parser;
	private String projectDir;
	private RenamedComponentsDetector rcd;
	
	public MetricsExtractor(Git git, IMetricFileBuilder mfb) throws IOException {
		this.git = git;
		this.mfb = mfb;
		this.projectDir = git.getRepository().getDirectory().getParentFile().getAbsolutePath();
		this.parser = new Parser(this.projectDir);
		this.rcd = new RenamedComponentsDetector();
	}
	
	public void extractAtCommit(String sha, String[] dirs, String outputDir) throws Exception {
		extractFromCommit(sha, dirs, outputDir, 1);
	}
	
	public void extractBetweenCommits(String sha1, String sha2, String[] dirs, String outputDir) throws Exception {
		populateSystem(sha1, dirs);
		
		// Retrieve commits in the range [sha1, sha2]
		ObjectId from = git.getRepository().resolve(sha1);
		ObjectId to = git.getRepository().resolve(sha2);
		Iterator<RevCommit> iteratorOnCommits = git.log().addRange(from, to).call().iterator();
		
		extractMetricsHistory(iteratorOnCommits, outputDir, 100000);
	}
	
	public void extractFromCommit(String sha, String[] dirs, String outputDir, int maxLength) throws Exception {
		populateSystem(sha, dirs);
        
        // Retrieve all prior commits
        ObjectId head = git.getRepository().resolve(Constants.HEAD);
        Iterator<RevCommit> iteratorOnCommits = git.log().add(head).call().iterator(); 
        
        extractMetricsHistory(iteratorOnCommits, outputDir, maxLength);	
	}
	
	private void extractMetricsHistory(Iterator<RevCommit> commits, String outputDir, int maxLength) throws Exception { 
        int count = 0;
        boolean changed = true;
        RevCommit currentCommit = commits.next();
        System.out.println("Start mining history ...");
        while(count < maxLength && commits.hasNext()) {
			RevCommit previousCommit = commits.next();
			
			if (changed) {
				// Extract metrics
				mfb.handleRenamedComponents(rcd.getRenamedComponents());
				
				String metricFilePath = outputDir + String.format("/commit_%d.csv", count); 
				count = mfb.buildMetricFile(metricFilePath) == true ? count+1 : count;
				
				changed = false;
				rcd.clear();
			}
			
			// Update the system if necessary
			changed = updateSystem(previousCommit, currentCommit);
			currentCommit = previousCommit;
		}
	}
	
	private void checkout(String sha) throws Exception {
		git.checkout().setName(sha).call();
		parser.updateSourcepathEntries();
	}
	
	private void populateSystem(String sha, String[] dirs) throws Exception {
		String systemName = git.getRepository().getDirectory().getParentFile().getName();
		System.out.println("Building system model for " + systemName + " ...");
		// Checkout
        checkout(sha);
        
        // Populate the system to mirror the current system's snapshot
		for (int i=0;i<dirs.length;i++) {
			Collection<File> filesInDirectory = FileUtils.listFiles(new File(projectDir + '/' + dirs[i]), new String[]{"java"}, true);
			for (File file : filesInDirectory) {
				SystemObject.getInstance().addFile(parser.parseFile(file));
			}
		}
	}
	
	private boolean updateSystem(RevCommit previousCommit, RevCommit currentCommit) throws Exception {
		final TreeWalk tw = new TreeWalk(git.getRepository());
		tw.setRecursive(true);
		tw.addTree(previousCommit.getTree());
		tw.addTree(currentCommit.getTree());
		
		final RenameDetector rd = new RenameDetector(git.getRepository());
    	rd.addAll(DiffEntry.scan(tw));
    	
    	boolean changed = false;
		for (DiffEntry diff : rd.compute(tw.getObjectReader(), null)) {
			ChangeType changeType = diff.getChangeType();
			String currentPath = diff.getNewPath();
			String previousPath = diff.getOldPath();
			switch (changeType) {
				case ADD:
					if (currentPath.endsWith(".java")) {
						FileObject currentFile = SystemObject.getInstance().getFileByPath(currentPath);
						if (currentFile != null) {
							SystemObject.getInstance().removeFile(currentFile);
							changed = true;
						}
					}
					break;
				case RENAME:
					if (!previousPath.startsWith(".java")) {
						break;
					}
				case MODIFY:
					if (currentPath.endsWith(".java")) {
						FileObject currentFile = SystemObject.getInstance().getFileByPath(currentPath);
						if (currentFile != null) {
							if (!git.getRepository().resolve(Constants.HEAD).name().equals(previousCommit.name())) {
								checkout(previousCommit.name());
							}
							FileObject previousFile = parser.parseFile(new File(projectDir + "/" + previousPath));
							rcd.detectRenamedComponents(currentFile, previousFile);
							SystemObject.getInstance().removeFile(currentFile);
							SystemObject.getInstance().addFile(previousFile);
							changed = true;
						}
					}
					break;
				default: break;
			}
		}
		return changed;
	}
}
