package org.ab.metrics;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.FieldObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;

/*
 * DISTANCE: the Jaccard distance between a method and a class.
 */

public class DISTANCE {
	
	public static double compute(MethodObject m, ClassObject c) {
		Set<String> methodSet = getEntitySet(m);
		Set<String> classSet = getEntitySet(c);
		
		classSet.remove(m.getName());
		
		return getDistance(methodSet, classSet);
	}
	
	private static Set<String> getEntitySet(ClassObject c) {
		Set<String> entitySet = new HashSet<String>();
		for (FieldObject field: c.getFields()) {
			if (!field.getModifiers().contains("static")) {
				entitySet.add(field.getName());
			}
		}
		
		for (MethodObject method: c.getMethods()) {
			if (!method.getModifiers().contains("static") && !method.isAccessor()) {
				entitySet.add(method.getName());
			}
		}
		return entitySet;
	}
	
	private static Set<String> getEntitySet(MethodObject m) {
		SystemObject s = SystemObject.getInstance();
		
		Set<String> entitySet = new HashSet<String>();
		for (String accessedFieldName: m.getAccessedFields()) {
			FieldObject accessedField = s.getFieldByName(accessedFieldName);
			if (accessedField != null && !accessedField.getModifiers().contains("static")) {
				entitySet.add(accessedFieldName);
			}
		}
		
		for (String invokedMethodName: m.getInvokedMethods()) {
			MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
			if (invokedMethod != null && !m.getName().equals(invokedMethodName) && !invokedMethod.getModifiers().contains("static")) {
				if (invokedMethod.isAccessor()) {
					entitySet.addAll(invokedMethod.getAccessedFields());
				}else {
					entitySet.add(invokedMethodName);
				}
			}
		}
		return entitySet;
	}
	
	private static double getDistance(Set<String> set1, Set<String> set2) {
        if(set1.isEmpty() && set2.isEmpty())
            return 1.0;
        return 1.0 - (double)intersection(set1,set2).size()/(double)union(set1,set2).size();
    }

    private static Set<String> union(Set<String> set1, Set<String> set2) {
        Set<String> set = new HashSet<String>();
        set.addAll(set1);
        set.addAll(set2);
        return set;
    }

    private static Set<String> intersection(Set<String> set1, Set<String> set2) {
        Set<String> set = new HashSet<String>();
        set.addAll(set1);
        set.retainAll(set2);
        return set;
    }
}
