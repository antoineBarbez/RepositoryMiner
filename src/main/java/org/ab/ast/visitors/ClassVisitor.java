package org.ab.ast.visitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ab.ast.AttributeObject;
import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ClassVisitor extends ASTVisitor {
	private ClassObject classObject;
	
	public ClassVisitor(String className) {
		this.classObject = new ClassObject(className);
	}
	
	public ClassObject getClassObject() {
		return this.classObject;
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		for (VariableDeclarationFragment vdf : (List<VariableDeclarationFragment>) node.fragments()) {
			AttributeObject attributeObject = new AttributeObject(vdf.getName().getIdentifier());
			classObject.addAttribute(attributeObject);
		}
		
		return true;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		// Construct the method's name.
		List<String> params = new ArrayList<String>();
		for (SingleVariableDeclaration var : (List<SingleVariableDeclaration>) node.parameters()) {
			params.add(var.getType().toString());
		}
		
		Collections.sort(params, String.CASE_INSENSITIVE_ORDER);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(node.getName().getIdentifier());
		buffer.append("(");
		buffer.append(String.join(", ", params));
		buffer.append(")");
		String methodName = buffer.toString();
				
		// Visit the body of the method.
		MethodVisitor visitor = new MethodVisitor(methodName, node);
		
		if (node.getBody() != null) {
			node.getBody().accept(visitor);
		}
		
		classObject.addMethod(visitor.getMethodObject());
		
		return true;
	}

}
