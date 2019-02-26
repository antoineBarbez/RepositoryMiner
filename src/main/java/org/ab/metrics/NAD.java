package org.ab.metrics;

import org.ab.ast.ClassObject;

/*
 * NAD: Number of Attributes Declared.
 */

public class NAD {

	public static int compute(ClassObject c) {
		return c.getFields().size();
	}
}
