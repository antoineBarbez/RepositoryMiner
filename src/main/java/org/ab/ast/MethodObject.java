package org.ab.ast;

import java.util.HashSet;
import java.util.Set;

public class MethodObject {
	private ClassObject declaringClass;
	private String name;
	private String body;
	private boolean constructor;
	
	public Set<String> accessedAttributes = new HashSet<String>();
	public Set<String> invokedMethods = new HashSet<String>();
	
	public MethodObject(String name, String body, boolean constructor) {
		this.name = name;
		this.body = body;
		this.constructor = constructor;
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
	
	public String getBody() {
		return this.body;
	}
	
	public boolean isConstructor() {
		return this.constructor;
	}
	
	public boolean isAccessor() {
		if (body.split("\r\n|\r|\n+").length >1) {
			return false;
		}
		
		if (name.startsWith("get") || name.startsWith("set") || name.startsWith("is")) {
			return true;
		}
		
		return false;
	}
}
