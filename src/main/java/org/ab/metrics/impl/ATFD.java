package org.ab.metrics.impl;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.FieldObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;
import org.ab.metrics.IUnaryClassMetric;
import org.ab.metrics.IUnaryMethodMetric;

public class ATFD implements IUnaryClassMetric, IUnaryMethodMetric {
	public double compute(ClassObject c) {
		SystemObject s = SystemObject.getInstance();
		
		Set<String> foreignData = new HashSet<String>();
		for (MethodObject m: c.getMethods()) {
			for (String accessedFieldName: m.getAccessedFields()) {
				FieldObject accessedField = s.getFieldByName(accessedFieldName);
				if (accessedField != null && !accessedField.getDeclaringClass().equals(c)) {
					foreignData.add(accessedFieldName);
				}
			}
			
			for (String invokedMethodName: m.getInvokedMethods()) {
				MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
				if (invokedMethod != null && invokedMethod.isAccessor() && !invokedMethod.getDeclaringClass().equals(c)) {
					foreignData.add(invokedMethodName);
				}
			}
		}
		return foreignData.size();
	}
	
	public double compute(MethodObject m) {
		SystemObject s = SystemObject.getInstance();
		
		Set<String> foreignData = new HashSet<String>();
		for (String accessedFieldName: m.getAccessedFields()) {
			FieldObject accessedField = s.getFieldByName(accessedFieldName);
			if (accessedField != null && !accessedField.getDeclaringClass().equals(m.getDeclaringClass())) {
				foreignData.add(accessedFieldName);
			}
		}
		
		for (String invokedMethodName: m.getInvokedMethods()) {
			MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
			if (invokedMethod != null && invokedMethod.isAccessor() && !invokedMethod.getDeclaringClass().equals(m.getDeclaringClass())) {
				foreignData.add(invokedMethodName);
			}
		}
		
		return foreignData.size();
	}
	
	public String getName() {
		return "ATFD";
	}
}
