package org.ab.metrics;

import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;

/*
 * LOC: Lines Of Code.
 * Ignores comments and blank lines
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
			return 1;
		}
		 
		// Block.toString() already suppress comments and blank lines
		// so they don't have to be handled in the regex.
		return m.getBody().toString().split("\r\n|\r|\n").length;
	}
}
