package org.ab.metrics.impl;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.FieldObject;
import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;
import org.ab.metrics.IUnaryClassMetric;

public class NADC implements IUnaryClassMetric {

	public double compute(ClassObject c) {
		SystemObject s = SystemObject.getInstance();
		
		Set<ClassObject> accessedDataClasses = new HashSet<ClassObject>();
		for (MethodObject m: c.getMethods()) {
			for (String af: m.getAccessedFields()) {
				FieldObject afo = s.getFieldByName(af);
				if (afo != null) {
					ClassObject ac = afo.getDeclaringClass();
					if (ac.isDataClass()) {
						accessedDataClasses.add(ac);
					}
				}
			}
			
			for (String im: m.getInvokedMethods()) {
				MethodObject imo = s.getMethodByName(im);
				if (imo != null) {
					ClassObject ac = imo.getDeclaringClass();
					if (ac.isDataClass()) {
						accessedDataClasses.add(ac);
					}
				}
			}
		}
		return accessedDataClasses.size();
	}

	public String getName() {
		return "NADC";
	}
}
