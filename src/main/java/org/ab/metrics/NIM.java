package org.ab.metrics;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;

/*
 * NIM: Number of Invoked Methods.
 * Number of distinct non-accessor methods of a class invoked by a method.
 */

public class NIM {
	
	public static int compute(MethodObject m, ClassObject c) {
		SystemObject s = SystemObject.getInstance();
		
		Set<String> invokedMethods = new HashSet<String>();
		
		for (String invokedMethodName: m.getInvokedMethods()) {
			MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
			if (invokedMethod != null && !invokedMethod.isAccessor() && invokedMethod.getDeclaringClass().getName().equals(c.getName())) {
				invokedMethods.add(invokedMethodName);
			}
		}
		return invokedMethods.size();
	}
}
