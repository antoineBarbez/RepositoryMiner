package org.ab.ast.parser;

import java.util.ArrayList;
import java.util.List;

import org.ab.ast.MethodObject;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

public class MethodVisitor extends ASTVisitor {
	private MethodObject methodObject;
	
	public MethodVisitor(MethodObject methodObject) {
		this.methodObject = methodObject;
	}
	
	public MethodObject getMethodObject() {
		return this.methodObject;
	}
	
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		return false;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		IBinding bind = node.resolveBinding();
		if (bind == null) {
			return true;
		}
		
		if (bind.getKind() == IBinding.VARIABLE) {
			IVariableBinding vBind = (IVariableBinding) bind;
			if (vBind.isField()) {
				ITypeBinding cBind = vBind.getDeclaringClass();
				if (cBind != null && !isBuiltIn(cBind)) {
					methodObject.addAccessedField(cBind.getTypeDeclaration().getQualifiedName() + "." + vBind.getName());
				}
			}
		}else if (bind.getKind() == IBinding.METHOD) {
			IMethodBinding mBind = (IMethodBinding) bind;
			ITypeBinding cBind = mBind.getDeclaringClass();
			if (cBind != null && !isBuiltIn(cBind)) {
				methodObject.addInvokedMethod(constructMethodName(mBind));
			}
		}
		return true;
	}
	
	private String constructMethodName(IMethodBinding mBind) {
		List<String> parameters = new ArrayList<>();
		for (ITypeBinding type : mBind.getParameterTypes()) {
			parameters.add(type.getName());
		}
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(mBind.getDeclaringClass().getTypeDeclaration().getQualifiedName());
		buffer.append(".");
		buffer.append(mBind.getName());
		buffer.append("(");
		buffer.append(String.join(", ", parameters));
		buffer.append(")");
		
		return buffer.toString();
	}
	
	private boolean isBuiltIn(ITypeBinding cBind) {
		String type = cBind.getQualifiedName();
		if (type.startsWith("java.") || type.startsWith("javax.")) {
			return true;
		}
		return false;
	}
}
