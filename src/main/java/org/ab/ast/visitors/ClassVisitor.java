package org.ab.ast.visitors;

import java.util.ArrayList;
import java.util.List;

import org.ab.ast.FieldObject;
import org.ab.ast.ClassObject;
import org.ab.ast.InnerClassObject;
import org.ab.ast.MethodObject;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
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
	public boolean visit(AnonymousClassDeclaration node) {
		return false;
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
		
		c.setInterface(node.isInterface());
		
		for (Object modifier: node.modifiers()) {
			c.addModifier(modifier.toString());
		}
		
		for (FieldObject f: c.getFields()) {
			f.setDeclaringClass(c);
		}
		
		for (MethodObject m: c.getMethods()) {
			m.setDeclaringClass(c);
		}
		
		for (InnerClassObject ic: c.getInnerClasses()) {
			ic.setDeclaringClass(c);
		}
		
		classObject.addInnerClass(c);
		
		return false;
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {		
		for (VariableDeclarationFragment vdf : (List<VariableDeclarationFragment>) node.fragments()) {
			FieldObject field = new FieldObject(classObject.getName() + "." + vdf.getName().getIdentifier());
			
			for (Object modifier: node.modifiers()) {
				field.addModifier(modifier.toString());
			}
			
			classObject.addField(field);
		}
		
		return true;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		// Construct the method's name.
		List<String> params = new ArrayList<String>();
		for (SingleVariableDeclaration svd : (List<SingleVariableDeclaration>) node.parameters()) {
			params.add(svd.getType().toString());
		}
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(classObject.getName() + "." + node.getName().getIdentifier());
		buffer.append("(");
		buffer.append(String.join(", ", params));
		buffer.append(")");
		String methodName = buffer.toString();
		
		MethodObject method;
		if (node.getBody() != null) {
			// Visit the body of the method.
			MethodVisitor visitor = new MethodVisitor(methodName);
			node.getBody().accept(visitor);
			method = visitor.getMethodObject();
			method.setBody(node.getBody().toString());
		}else{
			method = new MethodObject(methodName);
		}
		
		method.setConstructor(node.isConstructor());
		
		for (Object modifier: node.modifiers()) {
			method.addModifier(modifier.toString());
		}
		
		classObject.addMethod(method);
		
		return true;
	}

}
