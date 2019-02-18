package org.ab.ast;

import java.util.HashSet;
import java.util.Set;

public class MethodObject extends CodeComponent {
	private String body = "";
	private boolean constructor = false;
	private boolean accessor = false;
	private ClassObject declaringClass = null;
	private Set<String> accessedFields = new HashSet<String>();
	private Set<String> invokedMethods = new HashSet<String>();
	
	public MethodObject(String name) {
		super(name);
	}
	
	public void addAccessedField(String field) {
		accessedFields.add(field);
	}
	
	public void addInvokedMethod(String method) {
		invokedMethods.add(method);
	}
	
	public Set<String> getAccessedFields () {
		return accessedFields;
	}
	
	public String getBody() {
		return body;
	}
	
	public ClassObject getDeclaringClass() {
		return declaringClass;
	}
	
	public Set<String> getInvokedMethods() {
		return invokedMethods;
	}
	
	public boolean isAccessor() {
		return accessor;
	}
	
	public boolean isConstructor() {
		return constructor;
	}
	
	public void setAccessor(boolean accessor) {
		this.accessor = accessor;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public void setConstructor(boolean constructor) {
		this.constructor = constructor;
	}
	
	public void setDeclaringClass(ClassObject declaringClass) {
		this.declaringClass = declaringClass;
	}
}
