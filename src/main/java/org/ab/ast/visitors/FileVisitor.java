package org.ab.ast.visitors;

import org.ab.ast.FileObject;
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
		// Visit the class.
		ClassVisitor visitor = new ClassVisitor(node.getName().getFullyQualifiedName());
		node.accept(visitor);
		
		fileObject.addClass(visitor.getClassObject());
		
		return true;
	}

}
