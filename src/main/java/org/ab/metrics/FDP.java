package org.ab.metrics;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.FieldObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;
import org.ab.util.MetricUtils;

/*
 * FDP: Foreign Data Providers.
 * Number of distinct unrelated classes whose attributes are accessed.  
 */

public class FDP {

	public static int compute(ClassObject c) {
		Set<String> foreignDataProviders = new HashSet<String>();
		for (MethodObject m: c.getMethods()) {
			foreignDataProviders.addAll(getForeignDataProviders(m));
		}
		return foreignDataProviders.size();
	}
	
	public static int compute(MethodObject m) {
		return getForeignDataProviders(m).size();
	}
	
	private static Set<String> getForeignDataProviders(MethodObject m) {
		SystemObject s = SystemObject.getInstance();
		ClassObject delaringClass = m.getDeclaringClass();
		Set<String> foreignDataProviders = new HashSet<String>();
		for (String accessedFieldName: m.getAccessedFields()) {
			FieldObject accessedField = s.getFieldByName(accessedFieldName);
			if (accessedField != null) {
				if (!delaringClass.isRelatedTo(accessedField.getDeclaringClass())) {
					foreignDataProviders.add(accessedField.getDeclaringClass().getName());
				}
			}else {
				String accessedFieldDeclaringClass = MetricUtils.getDeclaringClassName(accessedFieldName);
				if (accessedFieldDeclaringClass != null) {
					foreignDataProviders.add(accessedFieldDeclaringClass);
				}
			}
		}
		
		for (String invokedMethodName: m.getInvokedMethods()) {
			MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
			if (invokedMethod != null) {
				if (invokedMethod.isAccessor() && !delaringClass.isRelatedTo(invokedMethod.getDeclaringClass())) {
					foreignDataProviders.add(invokedMethod.getDeclaringClass().getName());
				}
			}else {
				String accessedFieldDeclaringClass = MetricUtils.getDeclaringClassName(invokedMethodName); 
				if (accessedFieldDeclaringClass != null) {
					foreignDataProviders.add(accessedFieldDeclaringClass);
				}
			}
		}
		return foreignDataProviders;
	}
}
