package org.ab.ast.visitors;

import org.ab.ast.FieldObject;
import org.ab.ast.ClassObject;
import org.ab.ast.FileObject;
import org.ab.ast.InnerClassObject;
import org.ab.ast.MethodObject;
import org.eclipse.jdt.core.dom.ASTVisitor;
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
		fileObject.setPackageName(packageName);
		return true;
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		//Ignore inner classes, they will be processed by the ClassVisitor.
		if (node.isMemberTypeDeclaration()) {
			return true;
		}
		
		// Visit the class.
		ClassVisitor visitor = new ClassVisitor(node.getName().resolveTypeBinding().getQualifiedName(), false);
		node.accept(visitor);
		
		ClassObject c = visitor.getClassObject();
		
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
		
		fileObject.addClass(c);
		
		return true;
	}

}
