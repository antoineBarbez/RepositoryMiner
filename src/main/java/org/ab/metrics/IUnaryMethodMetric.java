package org.ab.metrics;

import org.ab.ast.MethodObject;

public interface IUnaryMethodMetric extends IMetric {
	public double compute(MethodObject m);
}
