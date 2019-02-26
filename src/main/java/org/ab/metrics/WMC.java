package org.ab.metrics;

import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;

/*
 * WMC: Weighted Method Count 
 */

public class WMC {
	
	public static int compute(ClassObject c) {
		int wmc = 0;
		for (MethodObject m: c.getMethods()) {
			wmc += CYCLO.compute(m);
		}
		return wmc;
	}
}
