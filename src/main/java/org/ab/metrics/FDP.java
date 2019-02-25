package org.ab.metrics;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.FieldObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;

/*
 * FDP: Foreign Data Providers.
 * Number of distinct unrelated classes whose attributes are accessed.  
 */

public class FDP {

	public static double compute(ClassObject c) {
		SystemObject s = SystemObject.getInstance();
		
		Set<String> foreignDataProviders = new HashSet<String>();
		for (MethodObject m: c.getMethods()) {
			for (String accessedFieldName: m.getAccessedFields()) {
				FieldObject accessedField = s.getFieldByName(accessedFieldName);
				if (accessedField != null && !c.isRelatedTo(accessedField.getDeclaringClass())) {
					foreignDataProviders.add(accessedField.getDeclaringClass().getName());
				}
			}
			
			for (String invokedMethodName: m.getInvokedMethods()) {
				MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
				if (invokedMethod != null && invokedMethod.isAccessor() && !c.isRelatedTo(invokedMethod.getDeclaringClass())) {
					foreignDataProviders.add(invokedMethod.getDeclaringClass().getName());
				}
			}
		}
		return foreignDataProviders.size();
	}
	
	public static double compute(MethodObject m) {
		SystemObject s = SystemObject.getInstance();
		ClassObject delaringClass = m.getDeclaringClass();
		Set<String> foreignDataProviders = new HashSet<String>();
		for (String accessedFieldName: m.getAccessedFields()) {
			FieldObject accessedField = s.getFieldByName(accessedFieldName);
			if (accessedField != null && !delaringClass.isRelatedTo(accessedField.getDeclaringClass())) {
				foreignDataProviders.add(accessedField.getDeclaringClass().getName());
			}
		}
		
		for (String invokedMethodName: m.getInvokedMethods()) {
			MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
			if (invokedMethod != null && invokedMethod.isAccessor() && !delaringClass.isRelatedTo(invokedMethod.getDeclaringClass())) {
				foreignDataProviders.add(invokedMethod.getDeclaringClass().getName());
			}
		}
		
		return foreignDataProviders.size();
	}
}
