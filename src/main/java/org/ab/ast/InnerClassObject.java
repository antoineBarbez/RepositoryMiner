package org.ab.ast;

public class InnerClassObject extends ClassObject {
	private ClassObject declaringClass;
	
	public InnerClassObject(String identifier) {
		super(identifier);
	}
	
	public ClassObject getDeclaringClass() {
		return this.declaringClass;
	}
	
	@Override
	public String getName() {
		return declaringClass.getName() + "." + getIdentifier();
	}
	
	@Override
	public boolean isRelatedTo(ClassObject aClass) {
		if (super.isRelatedTo(aClass)) {
			return true;
		}
		
		if (aClass.inheritFrom(declaringClass) || declaringClass.inheritFrom(aClass)) {
			return true;
		}
		
		return false;
	}
	
	public void setDeclaringClass(ClassObject declaringClass) {
		this.declaringClass = declaringClass;
	}
}
