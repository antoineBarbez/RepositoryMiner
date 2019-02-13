package org.ab.metrics;

import org.ab.ast.ClassObject;

public interface IUnaryClassMetric extends IMetric {
	public double compute(ClassObject c);
}
