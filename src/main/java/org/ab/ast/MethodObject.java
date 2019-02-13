package org.ab.ast;

public class MethodObject {
	private String name;
	private String body;
	private boolean constructor;
	
	public MethodObject(String name, String body, boolean constructor) {
		this.name = name;
		this.body = body;
		this.constructor = constructor;
	}
	
	public String getName() {
		return this.name;
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
