package org.ab.metrics.impl;

import org.ab.ast.AttributeObject;
import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;
import org.ab.metrics.IUnaryClassMetric;

public class LCOM5 implements IUnaryClassMetric {
	
	public double compute(ClassObject c) {
		if (c.getMethods().size() < 2 || c.getAttributes().size() ==0) {
			return 0;
		}
		
		int sum = 0;
		for (AttributeObject a: c.getAttributes()) {
			for (MethodObject m: c.getMethods()) {
				if (m.accessedAttributes.contains(a.getName())) {
					sum++;
				}
			}
		}
		
		final double num = c.getMethods().size() - (1f/(float)c.getAttributes().size())*sum;
		final double den = c.getMethods().size() - 1;
		final double lcom5Value = num / den;
		
		return lcom5Value;
	}
	
	public String getName() {
		return "LCOM5";
	}
}
