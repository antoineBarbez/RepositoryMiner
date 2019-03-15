package org.ab.ast.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ab.ast.FileObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Parser {
	private String projectFolder;
	private String[] sourcepathEntries;
	
	public Parser(String projectFolder) throws IOException {
		this.projectFolder = projectFolder;
		this.sourcepathEntries = getSourcepathEntries(projectFolder);
	}
	
	public FileObject parseFile(File file) throws IOException {
		String relativePath = file.getAbsolutePath().substring(projectFolder.length() + 1);
				
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
	
	public void updateSourcepathEntries() throws IOException {
		this.sourcepathEntries = getSourcepathEntries(projectFolder);
	}
	
	/**
	 * @param rootDirPath the repository directory.
	 * @return Returns all the sourcepath entries, i.e., the first parent directories of packages.
	 * Example: if a java file: "/.../project/src/java/org/package/MyClass.java" declares "package org.package;",
	 * Then, "/.../project/src/java/" is a sourcepath entry.  
	 */
	private String[] getSourcepathEntries(String rootDirPath) throws IOException {
		File rootDir = new File(rootDirPath);
		Set<String> sourcepathEntries = new HashSet<String>();
		for (File subDirectory: getSubDirectoriesRecursively(rootDir)) {
			String sourcepathEntry = getSourcepathEntry(subDirectory);
			if (sourcepathEntry != null) {
				sourcepathEntries.add(sourcepathEntry);
			}
		}
		return sourcepathEntries.toArray(new String[sourcepathEntries.size()]);
	}
	
	private Set<File> getSubDirectoriesRecursively(File directory) {
		Set<File> directories = new HashSet<File>();
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory() && !file.getName().equals(".git")) {
				directories.add(file);
				directories.addAll(getSubDirectoriesRecursively(file));
			}
		}
		return directories;
	}
	
	private String getSourcepathEntry(File directory) throws IOException {
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".java")) {
				String fileString = FileUtils.readFileToString(file, "UTF-8");
				Pattern p = Pattern.compile("package\\s+(.+);");
				Matcher m = p.matcher(fileString);
				if(m.find()) {
					String packageRelativePath = m.group(1).replaceAll("\\.", "/");
					String directoryPath = directory.getAbsolutePath();
					if (directoryPath.endsWith(packageRelativePath)) {
						String sourcePathEntry = directoryPath.substring(0, directoryPath.length() - packageRelativePath.length());
						File sourcePathEntryFile = new File(sourcePathEntry);
						if (sourcePathEntryFile.exists()) {
							return sourcePathEntry;
						}
					}
				}
			}
		}
		return null;
	}
}
