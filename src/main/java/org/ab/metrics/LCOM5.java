package org.ab.metrics;

import org.ab.ast.FieldObject;
import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;

/*
 * LCOM5: Lack of COhesion in Methods (Version 5).
 */

public class LCOM5 {
	
	public static double compute(ClassObject c) {
		if (c.getMethods().size() < 2 || c.getFields().size() ==0) {
			return 0;
		}
		
		int sum = 0;
		for (FieldObject f: c.getFields()) {
			for (MethodObject m: c.getMethods()) {
				if (m.getAccessedFields().contains(f.getName())) {
					sum++;
				}
			}
		}
		
		final double num = c.getMethods().size() - (1f/(float)c.getFields().size())*sum;
		final double den = c.getMethods().size() - 1;
		final double lcom5Value = num / den;
		
		return lcom5Value;
	}
}
