package org.ab.ast.visitors;

import java.util.ArrayList;
import java.util.List;

import org.ab.ast.MethodObject;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

public class MethodVisitor extends ASTVisitor {
	private MethodObject methodObject;
	
	public MethodVisitor(String name, MethodDeclaration node) {
		String body;
		if (node.getBody() == null) {
			body = "";
		}else {
			body = node.getBody().toString();
		}
		
		this.methodObject = new MethodObject(name, body, node.isConstructor());
	}
	
	public MethodObject getMethodObject() {
		return this.methodObject;
	}
	
	@Override 
	public boolean visit(FieldAccess node) {
		IVariableBinding fBind = node.resolveFieldBinding();
		if (fBind != null) {
			String declaringClass = fBind.getDeclaringClass().getQualifiedName();
			String attributeName = fBind.getName();
			methodObject.accessedAttributes.add(declaringClass + "." + attributeName);
		}
		
		return true;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding mBind = node.resolveMethodBinding();
		if (mBind != null) {
			String declaringClass = mBind.getDeclaringClass().getQualifiedName();
			
			List<String> params = new ArrayList<>();
			for (ITypeBinding paramType : mBind.getParameterTypes()) {
				params.add(paramType.getName());
			}
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(mBind.getName());
			buffer.append("(");
			buffer.append(String.join(", ", params));
			buffer.append(")");
			
			String methodName = buffer.toString();
			
			methodObject.invokedMethods.add(declaringClass + "." + methodName);
		}
		 
		return true;
	}
}
