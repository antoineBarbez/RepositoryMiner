package org.ab.metrics.impl;

import java.util.HashSet;
import java.util.Set;

import org.ab.ast.AttributeObject;
import org.ab.ast.ClassObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;
import org.ab.metrics.IUnaryClassMetric;

public class NADC implements IUnaryClassMetric {

	public double compute(ClassObject c) {
		SystemObject s = SystemObject.getInstance();
		
		Set<ClassObject> accessedDataClasses = new HashSet<ClassObject>();
		for (MethodObject m: c.getMethods()) {
			for (String aa: m.accessedAttributes) {
				AttributeObject aao = s.getAttributeByName(aa);
				if (aao != null) {
					ClassObject ac = aao.getDeclaringClass();
					if (ac.isDataClass()) {
						accessedDataClasses.add(ac);
					}
				}
			}
			
			for (String im: m.invokedMethods) {
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
