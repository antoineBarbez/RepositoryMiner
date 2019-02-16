package org.ab.ast;

import java.util.HashSet;
import java.util.Set;

public abstract class CodeComponent {
	private String name;
	private Set<String> modifiers = new HashSet<String>();
	
	public CodeComponent(String name) {
		this.name = name;
	}
	
	public void addModifier(String modifier) {
		modifiers.add(modifier);
	}
	
	public String getName() {
		return name;
	}
	
	public Set<String> getModifiers() {
		return modifiers;
	}
}
