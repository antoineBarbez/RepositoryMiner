package org.ab.metrics;

import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;

/*
 * LOC: Lines Of Code.
 */
public class LOC {
	
	public static int compute(ClassObject c) {
		int loc = 0;
		for (MethodObject m: c.getMethods()) {
			loc += compute(m);
		}
		return loc;
	}
	
	public static int compute(MethodObject m) {
		if (m.getBody() == null) {
			return 0;
		}
		
		return m.getBody().toString().split("\r\n|\r|\n+").length;
	}
}
