package org.ab.ast;

public class AttributeObject {
	private String name;
	private ClassObject declaringClass;
	
	public AttributeObject(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public ClassObject getDeclaringClass() {
		return this.declaringClass;
	}
	
	public void setDeclaringClass(ClassObject declaringClass) {
		this.declaringClass = declaringClass;
	}
}
