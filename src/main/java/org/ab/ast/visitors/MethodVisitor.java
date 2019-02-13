package org.ab.ast.visitors;

import org.ab.ast.MethodObject;
import org.eclipse.jdt.core.dom.ASTVisitor;

public class MethodVisitor extends ASTVisitor {
	private MethodObject methodObject;
	
	public MethodVisitor(String name, String body, boolean constructor) {
		this.methodObject = new MethodObject(name, body, constructor);
	}
	
	public MethodObject getMethodObject() {
		return this.methodObject;
	}
}
