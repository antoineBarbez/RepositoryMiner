package org.ab.ast.parser;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.FileObject;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;


public class FileVisitor extends ASTVisitor {
	private FileObject fileObject;
	
	public FileVisitor(String path) {
		this.fileObject = new FileObject(path);
	}
	
	public FileObject getFileObject() {
		return this.fileObject;
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		String packageName = node.getName().getFullyQualifiedName();
		fileObject.setPackage(packageName);
		return true;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		//Ignore inner classes, they will be processed by the ClassVisitor.
		if (node.isMemberTypeDeclaration()) {
			return true;
		}
		
		// Visit the class.
		ClassVisitor visitor = new ClassVisitor(node.getName().getIdentifier(), false);
		node.accept(visitor);
		
		ClassObject c = visitor.getClassObject();
		
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
		c.setPackage(fileObject.getPackage());
		fileObject.addClass(c);
		
		return true;
	}

}
