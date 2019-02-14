package org.ab.metrics.impl;

import org.ab.ast.ClassObject;
import org.ab.metrics.IUnaryClassMetric;

public class NAD implements IUnaryClassMetric {

	public double compute(ClassObject c) {
		return c.getAttributes().size();
	}

	public String getName() {
		return "NAD";
	}
}
