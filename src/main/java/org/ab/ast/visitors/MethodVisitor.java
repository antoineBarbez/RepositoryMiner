package org.ab.ast.visitors;

import org.ab.ast.MethodObject;
import org.eclipse.jdt.core.dom.ASTVisitor;

public class MethodVisitor extends ASTVisitor {
	private MethodObject methodObject;
	
	public MethodVisitor(String methodName) {
		this.methodObject = new MethodObject(methodName);
	}
	
	public MethodObject getMethodObject() {
		return this.methodObject;
	}
}
