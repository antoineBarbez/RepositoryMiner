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
	
	public boolean isDataClass() {
		int nbAttributes = attributes.size();
		int nbNonAccessorMethods = 0;
		for (MethodObject m: methods) {
			if (!m.isAccessor()) {
				nbNonAccessorMethods++;
			}
		}
		nbNonAccessorMethods = Integer.max(1, nbNonAccessorMethods);
		
		double ratio = nbAttributes/nbNonAccessorMethods;
		if (ratio >= 5) {
			return true;
		}
		return false;
	}
}
