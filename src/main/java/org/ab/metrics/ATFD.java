package org.ab.metrics;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.FieldObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;

/*
 * ATDF: Access To Foreign Data.
 * Number of distinct attributes of unrelated classes accessed directly or via accessor methods. 
 */

public class ATFD {
	
	public static double compute(ClassObject c) {
		SystemObject s = SystemObject.getInstance();
		
		Set<String> foreignData = new HashSet<String>();
		for (MethodObject m: c.getMethods()) {
			for (String accessedFieldName: m.getAccessedFields()) {
				FieldObject accessedField = s.getFieldByName(accessedFieldName);
				if (accessedField != null && !c.isRelatedTo(accessedField.getDeclaringClass())) {
					foreignData.add(accessedFieldName);
				}
			}
			
			for (String invokedMethodName: m.getInvokedMethods()) {
				MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
				if (invokedMethod != null && invokedMethod.isAccessor() && !c.isRelatedTo(invokedMethod.getDeclaringClass())) {
					foreignData.addAll(invokedMethod.getAccessedFields());
				}
			}
		}
		return foreignData.size();
	}
	
	public static double compute(MethodObject m) {
		SystemObject s = SystemObject.getInstance();
		ClassObject delaringClass = m.getDeclaringClass();
		Set<String> foreignData = new HashSet<String>();
		for (String accessedFieldName: m.getAccessedFields()) {
			FieldObject accessedField = s.getFieldByName(accessedFieldName);
			if (accessedField != null && !delaringClass.isRelatedTo(accessedField.getDeclaringClass())) {
				foreignData.add(accessedFieldName);
			}
		}
		
		for (String invokedMethodName: m.getInvokedMethods()) {
			MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
			if (invokedMethod != null && invokedMethod.isAccessor() && !delaringClass.isRelatedTo(invokedMethod.getDeclaringClass())) {
				foreignData.addAll(invokedMethod.getAccessedFields());
			}
		}
		
		return foreignData.size();
	}
}
