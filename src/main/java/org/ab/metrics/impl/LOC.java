package org.ab.metrics.impl;

import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;
import org.ab.metrics.IUnaryClassMetric;
import org.ab.metrics.IUnaryMethodMetric;

public class LOC implements IUnaryClassMetric, IUnaryMethodMetric {

	public double compute(ClassObject c) {
		double loc = 0;
		for (MethodObject m: c.getMethods()) {
			loc += compute(m);
		}
		return loc;
	}
	
	public double compute(MethodObject m) {
		String body = m.getBody();
		if (body.isEmpty()) {
			return 0;
		}
		
		return body.split("\r\n|\r|\n+").length;
	}
	
	public String getName() {
		return "LOC";
	}
}
