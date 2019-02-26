package org.ab.metrics;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.FieldObject;
import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;

/*
 * NADC: Number of Accessed Data Classes.
 */

public class NADC {
	
	public static int compute(ClassObject c) {
		SystemObject s = SystemObject.getInstance();
		
		Set<ClassObject> accessedDataClasses = new HashSet<ClassObject>();
		for (MethodObject m: c.getMethods()) {
			for (String accessedFieldName: m.getAccessedFields()) {
				FieldObject accessedField = s.getFieldByName(accessedFieldName);
				if (accessedField != null) {
					ClassObject accessedClass = accessedField.getDeclaringClass();
					if (accessedClass.isDataClass()) {
						accessedDataClasses.add(accessedClass);
					}
				}
			}
			
			for (String invokedMethodName: m.getInvokedMethods()) {
				MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
				if (invokedMethod != null) {
					ClassObject accessedClass = invokedMethod.getDeclaringClass();
					if (accessedClass.isDataClass()) {
						accessedDataClasses.add(accessedClass);
					}
				}
			}
		}
		return accessedDataClasses.size();
	}
	
	public static int compute(MethodObject m) {
		SystemObject s = SystemObject.getInstance();
		
		Set<ClassObject> accessedDataClasses = new HashSet<ClassObject>();
		for (String accessedFieldName: m.getAccessedFields()) {
			FieldObject accessedField = s.getFieldByName(accessedFieldName);
			if (accessedField != null) {
				ClassObject accessedClass = accessedField.getDeclaringClass();
				if (accessedClass.isDataClass()) {
					accessedDataClasses.add(accessedClass);
				}
			}
		}
		
		for (String invokedMethodName: m.getInvokedMethods()) {
			MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
			if (invokedMethod != null) {
				ClassObject accessedClass = invokedMethod.getDeclaringClass();
				if (accessedClass.isDataClass()) {
					accessedDataClasses.add(accessedClass);
				}
			}
		}
		return accessedDataClasses.size();
	}
}
