package org.ab.metrics;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.FieldObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;

/*
 * NAA: Number of Accessed Attributes.
 * Number of distinct attributes of a class accessed by a method.
 */
public class NAA {
	
	public static int compute(MethodObject m, ClassObject c) {
		SystemObject s = SystemObject.getInstance();
		
		Set<String> accessedAttributes = new HashSet<String>();
		for (String accessedFieldName: m.getAccessedFields()) {
			FieldObject accessedField = s.getFieldByName(accessedFieldName);
			if (accessedField != null && accessedField.getDeclaringClass().getName().equals(c.getName())) {
				accessedAttributes.add(accessedFieldName);
			}
		}
		
		for (String invokedMethodName: m.getInvokedMethods()) {
			MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
			if (invokedMethod != null && invokedMethod.isAccessor() && invokedMethod.getDeclaringClass().getName().equals(c.getName())) {
				accessedAttributes.addAll(invokedMethod.getAccessedFields());
			}
		}
		return accessedAttributes.size();
	}
}
