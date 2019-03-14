package org.ab;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.ab.ast.SystemObject;
import org.ab.ast.parser.Parser;
import org.ab.mfb.FeatureEnvyMetricFileBuilder;
import org.ab.mfb.GodClassMetricFileBuilder;
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
	private String projectDir;
	
	public MetricsExtractor(Git git) {
		this.git = git;
		this.projectDir = git.getRepository().getDirectory().getParentFile().getAbsolutePath();
	}
	
	public void extractAtCommit(String sha, String[] dirs, String outputDir) throws Exception {
		populateSystem(sha, dirs);
		
		// Extract metrics
		GodClassMetricFileBuilder classFileBuilder = new GodClassMetricFileBuilder();
		classFileBuilder.buildMetricFile(outputDir + "/class_metrics.csv");
		
		FeatureEnvyMetricFileBuilder methodFileBuilder = new FeatureEnvyMetricFileBuilder();
		methodFileBuilder.buildMetricFile(outputDir + "/method_metrics.csv");
	}
	
	public void extractFromCommit(String sha, String[] dirs, String outputDir) throws Exception {
		populateSystem(sha, dirs);
        
        // Retrieve all prior commits
        ObjectId head = git.getRepository().resolve(Constants.HEAD);
        Iterator<RevCommit> iteratorOnCommits = git.log().add(head).call().iterator(); 
        
        int count = 0;
        boolean changed = true;
        RevCommit currentCommit = iteratorOnCommits.next();
        System.out.println("Start mining history ...");
        while(iteratorOnCommits.hasNext()) {
			RevCommit previousCommit = iteratorOnCommits.next();
			
			if (changed) {
				// Extract metrics
				//GodClassMetricFileBuilder classFileBuilder = new GodClassMetricFileBuilder();
				//classFileBuilder.buildMetricFile(outputDir + "/class_metrics.csv");
				
				FeatureEnvyMetricFileBuilder methodFileBuilder = new FeatureEnvyMetricFileBuilder();
				methodFileBuilder.buildMetricFile(outputDir + "/test/commit_" + String.valueOf(count) + ".csv");
				
				count ++;
				changed = false;
			}
			
			// Update the system if necessary
			changed = updateSystem(previousCommit, currentCommit);
			currentCommit = previousCommit;
		}	
	}
	
	private void populateSystem(String sha, String[] dirs) throws Exception {
		System.out.println("Building system model ...");
		// Checkout
        git.checkout().setName(sha).call();
        
        // Populate the system to mirror the current system's snapshot
        Parser parser = new Parser(projectDir);
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
    	
    	Parser parser = null;
    	boolean changed = false;
		for (DiffEntry diff : rd.compute(tw.getObjectReader(), null)) {
			ChangeType changeType = diff.getChangeType();
			String newPath = diff.getNewPath();
			String oldPath = diff.getOldPath();
			switch (changeType) {
				case ADD:
					if (newPath.endsWith(".java") && SystemObject.getInstance().removeFile(newPath)) {
						changed = true;
					}
					break;
				case MODIFY:
					if (newPath.endsWith(".java") && SystemObject.getInstance().removeFile(newPath)) {
						if (parser == null) {
							git.checkout().setName(previousCommit.getName()).call();
							parser = new Parser(projectDir);
						}
						SystemObject.getInstance().addFile(parser.parseFile(new File(projectDir + "/" + newPath)));
						changed = true;
					}
					break;
				case RENAME:
					if (newPath.endsWith(".java") && oldPath.startsWith(".java") && SystemObject.getInstance().removeFile(newPath)) {
						if (parser == null) {
							git.checkout().setName(previousCommit.getName()).call();
							parser = new Parser(projectDir);
						}
						SystemObject.getInstance().addFile(parser.parseFile(new File(projectDir + "/" + oldPath)));
						changed = true;
					}
					break;
				default: break;
			}
		}
		
		return changed;
	}
}
