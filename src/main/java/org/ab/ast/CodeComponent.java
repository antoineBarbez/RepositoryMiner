package org.ab.ast;

import java.util.Set;

public abstract class CodeComponent {
	private String identifier;
	private Set<String> modifiers;
	
	public CodeComponent(String identifier) {
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public Set<String> getModifiers() {
		return modifiers;
	}
	
	public void setModifiers(Set<String> modifiers) {
		this.modifiers = modifiers;
	}
}
