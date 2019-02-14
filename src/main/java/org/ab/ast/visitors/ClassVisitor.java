package org.ab.ast.visitors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ab.ast.AttributeObject;
import org.ab.ast.ClassObject;
import org.ab.ast.InnerClassObject;
import org.ab.ast.MethodObject;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ClassVisitor extends ASTVisitor {
	private ClassObject classObject;
	
	public ClassVisitor(String className, boolean isInnerClass) {
		if (isInnerClass) {
			classObject = new InnerClassObject(className);
		}else {
			classObject = new ClassObject(className);
		}
	}
	
	public ClassObject getClassObject() {
		return this.classObject;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		String className = node.getName().resolveTypeBinding().getQualifiedName();
		// Ignore visiting the current class, infinite loop otherwise..
		if (className.equals(classObject.getName())) {
			return true;
		}
		// Visit the class.
		ClassVisitor visitor = new ClassVisitor(className, true);
		node.accept(visitor);
		
		InnerClassObject c = (InnerClassObject)visitor.getClassObject();
		
		for (AttributeObject a: c.getAttributes()) {
			a.setDeclaringClass(c);
		}
		
		for (MethodObject m: c.getMethods()) {
			m.setDeclaringClass(c);
		}
		
		for (InnerClassObject ic: c.getInnerClasses()) {
			ic.setDeclaringClass(c);
		}
		
		classObject.addInnerClass(c);
		
		return true;
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		for (VariableDeclarationFragment vdf : (List<VariableDeclarationFragment>) node.fragments()) {
			AttributeObject attributeObject = new AttributeObject(classObject.getName() + "." + vdf.getName().getIdentifier());
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
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(classObject.getName() + "." + node.getName().getIdentifier());
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
