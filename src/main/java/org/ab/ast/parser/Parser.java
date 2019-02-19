package org.ab.ast.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ab.ast.FileObject;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Parser {
	private String repoPath;
	private String[] sourcepathEntries;
	
	public Parser(String repoPath) {
		this.repoPath = repoPath;
		this.sourcepathEntries = getSourcepathEntries(repoPath);
	}
	
	public FileObject parseFile(File file) throws IOException {
		String relativePath = file.getAbsolutePath().substring(repoPath.length());
				
		ASTParser parser = ASTParser.newParser(AST.JLS10);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setCompilerOptions(JavaCore.getOptions());
		parser.setUnitName(relativePath);
		
		parser.setEnvironment(null, sourcepathEntries, null, true);
		parser.setSource(FileUtils.readFileToString(file, "UTF-8").toCharArray());
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		FileVisitor visitor = new FileVisitor(relativePath);
		cu.accept(visitor);
		
		return visitor.getFileObject();
	}
	
	public void updateSourcepathEntries() {
		sourcepathEntries = getSourcepathEntries(repoPath);
	}
	
	private String[] getSourcepathEntries(String rootDirPath) {
		File rootDir = new File(rootDirPath);
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
