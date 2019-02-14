package org.ab.metrics.impl;

import org.ab.ast.ClassObject;
import org.ab.metrics.IUnaryClassMetric;

public class NMD implements IUnaryClassMetric {

	public double compute(ClassObject c) {
		return c.getMethods().size();
	}
	
	public String getName() {
		return "NMD";
	}

}
