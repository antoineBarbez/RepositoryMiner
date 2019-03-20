package org.ab.metrics;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.ab.ast.ClassObject;
import org.ab.ast.FieldObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;
import org.ab.util.MetricUtils;

/*
 * ATDF: Access To Foreign Data.
 * Number of distinct attributes of unrelated classes accessed directly or via accessor methods. 
 */

public class ATFD {
	
	public static double compute(ClassObject c) {
		Set<String> foreignData = new HashSet<String>();
		for (MethodObject m: c.getMethods()) {
			foreignData.addAll(getAccessedForeignData(m));
		}
		return foreignData.size();
	}
	
	public static double compute(MethodObject m) {
		return getAccessedForeignData(m).size();
	}
	
	private static Set<String> getAccessedForeignData(MethodObject m) {
		SystemObject s = SystemObject.getInstance();
		ClassObject delaringClass = m.getDeclaringClass();
		Set<String> foreignData = new HashSet<String>();
		for (String accessedFieldName: m.getAccessedFields()) {
			FieldObject accessedField = s.getFieldByName(accessedFieldName);
			if (accessedField != null && delaringClass.isRelatedTo(accessedField.getDeclaringClass())) {
				continue;
			}
			foreignData.add(accessedFieldName);
		}
		
		for (String invokedMethodName: m.getInvokedMethods()) {
			MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
			if (invokedMethod != null) {
				if (invokedMethod.isAccessor() && !delaringClass.isRelatedTo(invokedMethod.getDeclaringClass())) {
					foreignData.addAll(invokedMethod.getAccessedFields());
				}
			}else {
				String accessedFieldName = MetricUtils.getAccessedFieldName(invokedMethodName); 
				if (accessedFieldName != null) {
					foreignData.add(accessedFieldName);
				}
			}
		}
		return foreignData;
	}
}
