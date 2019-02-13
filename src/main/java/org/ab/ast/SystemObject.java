package org.ab.ast;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ab.ast.visitors.FileVisitor;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class SystemObject {
	private static SystemObject INSTANCE = null;
	
	private Set<FileObject> files;
	private String repoPath;
	private String[] sourcepathEntries;
	
	private SystemObject() {
		this.files = new HashSet<FileObject>();
	}
	
	public static SystemObject getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SystemObject();
		}
		
		return INSTANCE;
	}
	
	public void initialize(String repoPath, String[] dirs) {
		this.repoPath = repoPath;
		
		this.sourcepathEntries = getSourcepathEntries();
		
		for (int i=0;i<dirs.length;i++) {
			Collection<File> filesInDirectory = FileUtils.listFiles(new File(repoPath + '/' + dirs[i]), new String[]{"java"}, true);
			for (File file : filesInDirectory) {
				try {
					addFile(file.getAbsolutePath());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
	}
	
	private void addFile(String absoluteFilePath) throws IOException {
		File file = new File(absoluteFilePath);
		if (!file.exists()) {
			return;
		}
		
		String fileString = FileUtils.readFileToString(file, "UTF-8");
		String projectName = repoPath.split("/")[repoPath.split("/").length-1];
		String fileName = "/" + projectName + absoluteFilePath.split(projectName)[absoluteFilePath.split(projectName).length -1];
				
		ASTParser parser = ASTParser.newParser(AST.JLS10);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setCompilerOptions(JavaCore.getOptions());
		parser.setUnitName(fileName);
		
		parser.setEnvironment(null, sourcepathEntries, null, true);
		parser.setSource(fileString.toCharArray());
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		FileVisitor visitor = new FileVisitor(fileName);
		cu.accept(visitor);
		
		files.add(visitor.getFileObject());
	}
	
	public Set<FileObject> getFiles() {
		return this.files;
	}
	
	public Set<ClassObject> getClasses() {
		Set<ClassObject> classes = new HashSet<ClassObject>();
		for (FileObject f: files) {
			classes.addAll(f.getClasses());
		}
		
		return classes;
	}
	
	private String[] getSourcepathEntries() {
		File rootDir = new File(repoPath);
		Set<String> sourcepathEntries = new HashSet<String>();
		Set<File> subDirectories = getDirectories(rootDir);
		for (File subDirectory: subDirectories) {
			String sourceDir=null;
			try {
				sourceDir = getSourceDir(subDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (sourceDir != null) {
				sourcepathEntries.add(sourceDir);
			}
		}
		
		return sourcepathEntries.toArray(new String[sourcepathEntries.size()]);
	}
	
	private Set<File> getDirectories(File directory) {
		Set<File> directories = new HashSet<File>();
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory() && !file.getName().equals(".git")) {
				directories.add(file);
				directories.addAll(getDirectories(file));
			}
		}
		return directories;
	}
	
	private String getSourceDir(File directory) throws IOException {
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".java")) {
				String fileString = FileUtils.readFileToString(file, "UTF-8");
				Pattern p = Pattern.compile("package\\s+(.+);");
				Matcher m = p.matcher(fileString);
				if(m.find()) {
					String sourcePath = "/"+ directory.getAbsolutePath().substring(1,directory.getAbsolutePath().length()- m.group(1).length());
					File sourceFile = new File(sourcePath);
					if (sourceFile.exists())
						return sourcePath;
				}
			}
		}
		return null;
		
	}
}
