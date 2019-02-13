package org.ab.metrics.impl;

import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;
import org.ab.metrics.IUnaryClassMetric;
import org.ab.metrics.IUnaryMethodMetric;

public class LOC implements IUnaryClassMetric, IUnaryMethodMetric {

	public double compute(ClassObject c) {
		// TODO
		return 0.0;
	}
	
	public double compute(MethodObject m) {
		// TODO
		return 0.0;
	}
	
	public String getName() {
		return "LOC";
	}
}
