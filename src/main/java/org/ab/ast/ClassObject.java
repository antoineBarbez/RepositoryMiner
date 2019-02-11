package org.ab.ast;

import java.util.HashSet;
import java.util.Set;

public class ClassObject {
	private String name;
	private Set<AttributeObject> attributes;
	private Set<MethodObject> methods;
	
	public ClassObject(String name) {
		this.name = name;
		this.attributes = new HashSet<AttributeObject>();
		this.methods = new HashSet<MethodObject>();
	}
	
	public void addAttribute(AttributeObject a) {
		this.attributes.add(a);
	}
	
	public void addMethod(MethodObject m) {
		this.methods.add(m);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Set<AttributeObject> getAttributes() {
		return this.attributes;
	}
	
	public Set<MethodObject> getMethods() {
		return this.methods;
	}
}
