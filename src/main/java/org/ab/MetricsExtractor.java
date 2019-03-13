package org.ab;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ab.ast.SystemObject;
import org.ab.ast.parser.Parser;
import org.ab.mfb.FeatureEnvyMetricFileBuilder;
import org.ab.mfb.GodClassMetricFileBuilder;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

public class MetricsExtractor {
	private Repository repository;
	
	public MetricsExtractor(Repository repository) {
		this.repository = repository;
	}
	
	public void extractAtCommit(String sha, String[] dirs, String outputDir) throws Exception {
		String projectDir = repository.getDirectory().getParentFile().getAbsolutePath();
		
		try (Git git = new Git(repository)) {
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
			
			// Extract metrics
			GodClassMetricFileBuilder classFileBuilder = new GodClassMetricFileBuilder();
			classFileBuilder.buildMetricFile(outputDir + "/class_metrics.csv");
			
			FeatureEnvyMetricFileBuilder methodFileBuilder = new FeatureEnvyMetricFileBuilder();
			methodFileBuilder.buildMetricFile(outputDir + "/method_metrics.csv");
		}
	}
	
	public void extractFromCommit(String sha, String[] dirs, String outputDir) throws Exception {
		String projectDir = repository.getDirectory().getParentFile().getAbsolutePath();
		
		try (Git git = new Git(repository)) {
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
	        
	        // Retrieve all prior commits
	        ObjectId head = repository.resolve(Constants.HEAD);
	        Iterator<RevCommit> iteratorOnCommits = git.log().add(head).call().iterator(); 
	        
	        boolean changed = true;
	        RevCommit currentCommit = iteratorOnCommits.next();
	        while(iteratorOnCommits.hasNext()) {
				RevCommit previousCommit = iteratorOnCommits.next();
				
				if (changed) {
					// Extract metrics
					
					changed = false;
				}
				
				// get changes at this commit
				Map<String, ChangeType> changes = getChanges(previousCommit, currentCommit);
				if (!changes.isEmpty()) {
					git.checkout().setName(previousCommit.getName()).call();
					parser.updateSourcepathEntries();
					SystemObject system = SystemObject.getInstance();
					
					for (Map.Entry<String, ChangeType> entry : changes.entrySet()) {
						String filePath = entry.getKey();
						ChangeType changeType = entry.getValue();
						
						switch (changeType) {
			    			case ADD:
			    				system.removeFile(filePath);
			    				break;
			    			case DELETE:
			    				system.addFile(parser.parseFile(new File(projectDir + "/" + filePath)));
			    				break;
			    			case MODIFY:
			    				system.removeFile(filePath);
			    				system.addFile(parser.parseFile(new File(projectDir + "/" + filePath)));
			    				break;
			    			default: break;
		    		
						}
					}
				}
				currentCommit = previousCommit;
			}
	    }	
	}
	
	public Map<String, ChangeType> getChanges(RevCommit previousCommit, RevCommit currentCommit) throws Exception {
		final TreeWalk tw = new TreeWalk(repository);
		tw.setRecursive(true);
		tw.addTree(previousCommit.getTree());
		tw.addTree(currentCommit.getTree());
		
		Map<String, ChangeType> changes = new HashMap<String, ChangeType>();
		for (DiffEntry diff : DiffEntry.scan(tw)) {
			ChangeType changeType = diff.getChangeType();
			String oldPath = diff.getOldPath();
    		String newPath = diff.getNewPath();
			
    		if (changeType == ChangeType.ADD) {
    			if (newPath.endsWith(".java")) {
    				changes.put(newPath, changeType);
    			}
    		}else {
    			if (oldPath.endsWith(".java")) {
    				changes.put(oldPath, changeType);
    			}
    		}
		}
		
		return changes;
	}
}
