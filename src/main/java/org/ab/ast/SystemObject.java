package org.ab.ast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SystemObject {
	private static SystemObject INSTANCE = null;
	
	private Set<FileObject> files = new HashSet<FileObject>();
	private Map<String, FileObject> fileMap = new HashMap<String, FileObject>();
	private Map<String, FieldObject> fieldMap = new HashMap<String, FieldObject>();
	private Map<String, MethodObject> methodMap = new HashMap<String, MethodObject>();
	private Map<String, ClassObject> classMap= new HashMap<String, ClassObject>();
	
	public static SystemObject getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SystemObject();
		}
		
		return INSTANCE;
	}
	
	/**
	 * Add a new file to the system. 
	 * @param file the new FileObject to be added.
	 */
	public void addFile(FileObject file) {
		files.add(file);
		fileMap.put(file.getPath(), file);
		for (TopLevelClassObject c: file.getTopLevelClasses()) {
			fillMapsRecursively(c);
		}
	}
	
	public void removeFile(FileObject file) {
		for (TopLevelClassObject c: file.getTopLevelClasses()) {
			emptyMapsRecursively(c);
		}
		fileMap.remove(file.getPath());
		files.remove(file);
	}
	
	private void fillMapsRecursively(ClassObject c) {
		classMap.put(c.getName(), c);
		for (FieldObject f: c.getFields()) {
			fieldMap.put(f.getName(), f);
		}
		for (MethodObject m: c.getMethods()) {
			methodMap.put(m.getName(), m);
		}
		for (InnerClassObject ic: c.getInnerClasses()) {
			fillMapsRecursively(ic);
		}
	}
	
	private void emptyMapsRecursively(ClassObject c) {
		classMap.remove(c.getName());
		for (FieldObject f: c.getFields()) {
			fieldMap.remove(f.getName());
		}
		for (MethodObject m: c.getMethods()) {
			methodMap.remove(m.getName());
		}
		for (InnerClassObject ic: c.getInnerClasses()) {
			emptyMapsRecursively(ic);
		}
	}
	
	public Set<FileObject> getFiles() {
		return this.files;
	}
	
	public Set<ClassObject> getClasses() {
		return new HashSet<ClassObject>(classMap.values());
	}
	
	public Set<MethodObject> getMethods() {
		return new HashSet<MethodObject>(methodMap.values());
	}
	
	public Set<FieldObject> getFields() {
		return new HashSet<FieldObject>(fieldMap.values());
	}
	
	public FileObject getFileByPath(String path) {
		if (fileMap.containsKey(path)) {
			return fileMap.get(path);
		}
		return null;
	}
	
	public FieldObject getFieldByName(String name) {
		if (fieldMap.containsKey(name)) {
			return fieldMap.get(name);
		}
		return null;
	}
	
	public MethodObject getMethodByName(String fullName) {
		if (methodMap.containsKey(fullName)) {
			return methodMap.get(fullName);
		}
		return null;
	}
	
	public ClassObject getClassByName(String name) {
		if (classMap.containsKey(name)) {
			return classMap.get(name);
		}
		return null;
	}
}
