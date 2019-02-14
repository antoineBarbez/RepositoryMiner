package org.ab.ast.visitors;

import java.util.HashMap;
import java.util.Map;

import org.ab.ast.AttributeObject;
import org.ab.ast.ClassObject;
import org.ab.ast.FileObject;
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
	
	public Map<String, AttributeObject> getAttributeMap() {
		Map<String, AttributeObject> attributeMap = new HashMap<String, AttributeObject>();
		
		for (ClassObject c: fileObject.getClasses()) {
			String classFullName = fileObject.getPackageName() + "." + c.getName();
			for (AttributeObject a: c.getAttributes()) {
				String attributeFullName = classFullName + "." + a.getName();
				attributeMap.put(attributeFullName, a);
			}
		}
		
		return attributeMap;
	}
	
	public Map<String, MethodObject> getMethodMap() {
		Map<String, MethodObject> methodMap = new HashMap<String, MethodObject>();
		
		for (ClassObject c: fileObject.getClasses()) {
			String classFullName = fileObject.getPackageName() + "." + c.getName();
			for (MethodObject m: c.getMethods()) {
				String methodFullName = classFullName + "." + m.getName();
				methodMap.put(methodFullName, m);
			}
		}
		
		return methodMap;
	}
	
	public Map<String, ClassObject> getClassMap() {
		Map<String, ClassObject> classMap = new HashMap<String, ClassObject>();
		
		for (ClassObject c: fileObject.getClasses()) {
			String classFullName = fileObject.getPackageName() + "." + c.getName();
			classMap.put(classFullName, c);
		}
		
		return classMap;
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
		//if (node.getName().getFullyQualifiedName().equals("LoaderManagerImpl")) {
			ClassVisitor visitor = new ClassVisitor(node.getName().getFullyQualifiedName());
			node.accept(visitor);
			
			ClassObject c = visitor.getClassObject();
			
			for (AttributeObject a: c.getAttributes()) {
				a.setDeclaringClass(c);
			}
			
			for (MethodObject m: c.getMethods()) {
				m.setDeclaringClass(c);
			}
			
			fileObject.addClass(c);
		//}
		
		return true;
	}

}
