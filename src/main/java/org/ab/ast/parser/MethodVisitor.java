package org.ab.ast.parser;

import java.util.ArrayList;
import java.util.List;

import org.ab.ast.MethodObject;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

public class MethodVisitor extends ASTVisitor {
	private MethodObject methodObject;
	
	public MethodVisitor(String name) {
		this.methodObject = new MethodObject(name);
	}
	
	public MethodObject getMethodObject() {
		return this.methodObject;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		IBinding bind = node.resolveBinding();
		if (bind != null) {
			if (bind.getKind() == IBinding.VARIABLE) {
				IVariableBinding vBind = (IVariableBinding) bind;
				
				if (vBind.isField()) {
					ITypeBinding cBind = vBind.getDeclaringClass();
					if (cBind != null) {
						String declaringClass;
						if (vBind.getDeclaringClass().isParameterizedType()) {
							declaringClass = vBind.getDeclaringClass().getTypeDeclaration().getQualifiedName();
						}else {
							declaringClass = vBind.getDeclaringClass().getQualifiedName();
						}
						
						methodObject.addAccessedField(declaringClass + "." + vBind.getName());
					}
				}
			}else if (bind.getKind() == IBinding.METHOD) {
				IMethodBinding mBind = (IMethodBinding) bind;
				
				String declaringClass;
				if (mBind.getDeclaringClass().isParameterizedType()) {
					declaringClass = mBind.getDeclaringClass().getTypeDeclaration().getQualifiedName();
				}else {
					declaringClass = mBind.getDeclaringClass().getQualifiedName();
				}

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
				
				methodObject.addInvokedMethod(declaringClass + "." + methodName);
			}
		}
	
		return true;
	}
}
