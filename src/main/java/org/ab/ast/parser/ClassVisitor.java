package org.ab.ast.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ab.ast.FieldObject;
import org.ab.ast.ClassObject;
import org.ab.ast.InnerClassObject;
import org.ab.ast.MethodObject;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
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
		// Ignore visiting the current class, infinite loop otherwise..
		if (node.getName().getIdentifier().equals(classObject.getIdentifier())) {
			return true;
		}
		
		// Visit the class.
		ClassVisitor visitor = new ClassVisitor(node.getName().getIdentifier(), true);
		node.accept(visitor);
		
		InnerClassObject c = (InnerClassObject)visitor.getClassObject();
		
		if (node.getSuperclassType() != null) {
			ITypeBinding bind = node.getSuperclassType().resolveBinding();
			c.setSuperClass(bind.getQualifiedName());
		}
		
		Set<String> modifiers = new HashSet<String>();
		for (Object modifier: node.modifiers()) {
			modifiers.add(modifier.toString());
		}
		
		c.setInterface(node.isInterface());
		c.setModifiers(modifiers);
		c.setDeclaringClass(classObject);
		classObject.addInnerClass(c);
		
		return false;
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {		
		for (VariableDeclarationFragment vdf : (List<VariableDeclarationFragment>) node.fragments()) {
			FieldObject field = new FieldObject(vdf.getName().getIdentifier());
			
			Set<String> modifiers = new HashSet<String>();
			for (Object modifier: node.modifiers()) {
				modifiers.add(modifier.toString());
			}
			
			field.setModifiers(modifiers);
			field.setDeclaringClass(classObject);
			classObject.addField(field);
		}
		
		return true;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		// Visit the body of the method.
		MethodVisitor visitor = new MethodVisitor(node.getName().getIdentifier());
		if (node.getBody() != null) {
			node.getBody().accept(visitor);
		}
		
		MethodObject method = visitor.getMethodObject();
		
		Set<String> modifiers = new HashSet<String>();
		for (Object modifier: node.modifiers()) {
			modifiers.add(modifier.toString());
		}
		
		List<String> params = new ArrayList<String>();
		for (SingleVariableDeclaration svd : (List<SingleVariableDeclaration>) node.parameters()) {
			params.add(svd.getType().toString());
		}
		
		method.setBody(node.getBody());
		method.setConstructor(node.isConstructor());
		method.setModifiers(modifiers);
		method.setParameters(params);
		method.setDeclaringClass(classObject);
		classObject.addMethod(method);
		
		return true;
	}
}
