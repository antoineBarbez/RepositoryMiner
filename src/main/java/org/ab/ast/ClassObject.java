package org.ab.ast;

import java.util.HashSet;
import java.util.Set;

public class ClassObject {
	private String name;
	private Set<AttributeObject> attributes = new HashSet<AttributeObject>();
	private Set<MethodObject> methods = new HashSet<MethodObject>();
	private Set<InnerClassObject> innerClasses = new HashSet<InnerClassObject>();
	
	public ClassObject(String name) {
		this.name = name;
	}
	
	public void addAttribute(AttributeObject a) {
		attributes.add(a);
	}
	
	public void addMethod(MethodObject m) {
		methods.add(m);
	}
	
	public void addInnerClass(InnerClassObject c) {
		innerClasses.add(c);
	}
	
	public Set<AttributeObject> getAttributes() {
		return attributes;
	}
	
	public Set<MethodObject> getMethods() {
		return methods;
	}
	
	public Set<InnerClassObject> getInnerClasses() {
		return innerClasses;
	}
	
	public String getName() {
		return this.name;
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
