package org.ab.metrics;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;

/*
 * NMD: Number of Methods Declared.
 * Counts only non-accessor methods.
 */

public class NMD {
	
	public static int compute(ClassObject c) {
		Set<String> nam = new HashSet<String>();
		for (MethodObject m: c.getMethods()) {
			if (!m.isAccessor()) {
				nam.add(m.getName());
			}
		}
		return nam.size();
	}
}
