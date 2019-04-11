package org.ab.ast;

import java.util.HashSet;
import java.util.Set;

public class FileObject {
	private String path;
	private String packageName;
	private Set<ClassObject> topLevelClasses  = new HashSet<ClassObject>();
	
	public FileObject(String path) {
		this.path = path;
	}
	
	public void addTopLevelClass(ClassObject c) {
		this.topLevelClasses.add(c);
	}
	
	public Set<ClassObject> getTopLevelClasses() {
		return this.topLevelClasses;
	}
	
	public String getPackage() {
		return packageName;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPackage(String packageName) {
		this.packageName = packageName;
	}
}