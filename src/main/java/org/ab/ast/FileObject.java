package org.ab.ast;

import java.util.HashSet;
import java.util.Set;

public class FileObject {
	private String path;
	private String packageName;
	private Set<ClassObject> classes  = new HashSet<ClassObject>();
	
	public FileObject(String path) {
		this.path = path;
	}
	
	public void addClass(ClassObject c) {
		this.classes.add(c);
	}
	
	public Set<ClassObject> getClasses() {
		return this.classes;
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