package org.ab;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.CodeComponent;
import org.ab.ast.FileObject;
import org.ab.ast.InnerClassObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;
import org.ab.ast.TopLevelClassObject;
import org.ab.ast.parser.Parser;
import org.ab.mfb.MetricFileBuilder;
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
	private MetricFileBuilder mfb;
	private Parser parser;
	private String projectDir;
	
	public MetricsExtractor(Git git, MetricFileBuilder mfb) throws IOException {
		this.git = git;
		this.mfb = mfb;
		this.projectDir = git.getRepository().getDirectory().getParentFile().getAbsolutePath();
		this.parser = new Parser(this.projectDir);
	}
	
	public void extractAtCommit(String sha, String[] dirs, String outputDir) throws Exception {
		populateSystem(sha, dirs);
		
		// Extract metrics
		mfb.buildMetricFile(outputDir + "metrics.csv");
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
				mfb.buildMetricFile(outputDir + "/commit_" + String.valueOf(count) + ".csv");
				
				count ++;
				changed = false;
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
		System.out.println("Building system model ...");
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
							checkForRenamedComponents(currentFile, previousFile);
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
	
	private void checkForRenamedComponents(FileObject initialFile, FileObject finalFile) {
		Map<String, String> renamedClasses = new HashMap<String, String>();
		Map<String, String> renamedMethods = new HashMap<String, String>();
		
		// Process top-level classes
		Set<TopLevelClassObject> initialTopLevelClasses = new HashSet<TopLevelClassObject>(initialFile.getTopLevelClasses());
		Set<TopLevelClassObject> finalTopLevelClasses = new HashSet<TopLevelClassObject>(finalFile.getTopLevelClasses());
		
		for (TopLevelClassObject ic : initialFile.getTopLevelClasses()) {
			TopLevelClassObject homonymClass;
			if ((homonymClass = (TopLevelClassObject)findComponentWithSameIdentifier(ic, finalTopLevelClasses)) != null) {
				if (!ic.getName().equals(homonymClass.getName())) {
					renamedClasses.put(ic.getName(), homonymClass.getName());
				}
				checkForRenamedComponents(ic, homonymClass, renamedClasses, renamedMethods);
				initialTopLevelClasses.remove(ic);
				finalTopLevelClasses.remove(homonymClass);
			}
		}
		
		// Check for similarity between remaining classes
		for (TopLevelClassObject ic : initialTopLevelClasses) {
			TopLevelClassObject similarClass;
			if ((similarClass = (TopLevelClassObject)findSimilarClass(ic, finalTopLevelClasses)) != null) {
				renamedClasses.put(ic.getName(), similarClass.getName());
				checkForRenamedComponents(ic, similarClass, renamedClasses, renamedMethods);
				finalTopLevelClasses.remove(similarClass);
			}
		}
		
		mfb.handleRenamedComponents(renamedClasses, renamedMethods);
		
	}
	
	private void checkForRenamedComponents(ClassObject initialClass, ClassObject finalClass, Map<String, String> renamedClasses, Map<String, String> renamedMethods) {
		// Process inner classes
		Set<InnerClassObject> initialInnerClasses = new HashSet<InnerClassObject>(initialClass.getInnerClasses());
		Set<InnerClassObject> finalInnerClasses = new HashSet<InnerClassObject>(finalClass.getInnerClasses());
		
		for (InnerClassObject ic : initialClass.getInnerClasses()) {
			InnerClassObject homonymClass;
			if ((homonymClass = (InnerClassObject)findComponentWithSameIdentifier(ic, finalInnerClasses)) != null) {
				if (!ic.getName().equals(homonymClass.getName())) {
					renamedClasses.put(ic.getName(), homonymClass.getName());
				}
				checkForRenamedComponents(ic, homonymClass, renamedClasses, renamedMethods);
				initialInnerClasses.remove(ic);
				finalInnerClasses.remove(homonymClass);
			}
		}
		
		// Check for similarity between remaining classes
		for (InnerClassObject ic: initialInnerClasses) {
			InnerClassObject similarClass;
			if ((similarClass = (InnerClassObject)findSimilarClass(ic, finalInnerClasses)) != null) {
				renamedClasses.put(ic.getName(), similarClass.getName());
				checkForRenamedComponents(ic, similarClass, renamedClasses, renamedMethods);
				finalInnerClasses.remove(similarClass);
			}
		}
		
		// Process methods
		Set<MethodObject> initialMethods = new HashSet<MethodObject>(initialClass.getMethods());
		Set<MethodObject> finalMethods = new HashSet<MethodObject>(finalClass.getMethods());
		
		for (MethodObject im : initialClass.getMethods()) {
			MethodObject homonymMethod;
			if ((homonymMethod = (MethodObject)findComponentWithSameIdentifier(im, finalMethods)) !=null) {
				if (!im.getName().equals(homonymMethod.getName())) {
					renamedMethods.put(im.getName(), homonymMethod.getName());
				}
				initialMethods.remove(im);
				finalMethods.remove(homonymMethod);
			}
		}
		
		// Check for similarity between remaining methods
		for (MethodObject im : initialMethods) {
			MethodObject similarMethod;
			if ((similarMethod = findSimilarMethod(im, finalMethods)) != null) {
				renamedMethods.put(im.getName(), similarMethod.getName());
				finalMethods.remove(similarMethod);
			}
		}	
	}
	
	private CodeComponent findComponentWithSameIdentifier(CodeComponent cc, Set<? extends CodeComponent> ccs) {
		String identifier = cc.getIdentifier();
		for (CodeComponent cci : ccs) {
			if (cci.getIdentifier().equals(identifier)) {
				return cci;
			}
		}
		return null;
	}
	
	private MethodObject findSimilarMethod(MethodObject m, Set<MethodObject> ms) {
		for (MethodObject mi : ms) {
			if (compare(m, mi)) {
				return mi;
			}
		}
		return null;
	}
	
	private ClassObject findSimilarClass(ClassObject c, Set<? extends ClassObject> cs) {
		for (ClassObject ci : cs) {
			if (compare(c, ci)) {
				return ci;
			}
		}
		return null;
	}
	
	private boolean compare(ClassObject c1, ClassObject c2) {
		// TODO
		return false;
	}
	
	private boolean compare(MethodObject m1, MethodObject m2) {
		// TODO
		return false;
	}
	
}
