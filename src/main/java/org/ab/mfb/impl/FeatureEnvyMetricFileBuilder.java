package org.ab.mfb.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.FieldObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;
import org.ab.metrics.DISTANCE;
import org.ab.metrics.FDP;
import org.ab.metrics.LOC;
import org.ab.metrics.NAA;
import org.ab.metrics.NAD;
import org.ab.metrics.NIM;
import org.ab.metrics.NMD;
import org.ab.mfb.BinaryMetricFileBuilder;
import org.ab.mfb.BinaryMetricFileBuilder.Tuple;

public class FeatureEnvyMetricFileBuilder extends BinaryMetricFileBuilder {

	@Override
	public List<Tuple<String, String>> getComponents() {
		SystemObject s = SystemObject.getInstance();
		
		List<Tuple<String, String>> tuples = new ArrayList<Tuple<String, String>>();
		for (MethodObject m: s.getMethods()) {
			if (!m.isAccessor() && !m.isConstructor()) {
				Set<ClassObject> accessedClasses = new HashSet<ClassObject>();
				for (String accessedFieldName: m.getAccessedFields()) {
					FieldObject accessedField = s.getFieldByName(accessedFieldName);
					if (accessedField != null && !m.getDeclaringClass().isRelatedTo(accessedField.getDeclaringClass())) {
						accessedClasses.add(accessedField.getDeclaringClass());
					}
				}
				
				for (String invokedMethodName: m.getInvokedMethods()) {
					MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
					if (invokedMethod != null && !m.getDeclaringClass().isRelatedTo(invokedMethod.getDeclaringClass())) {
						accessedClasses.add(invokedMethod.getDeclaringClass());
					}
				}
				
				if (accessedClasses.size() != 0) {
					for (ClassObject accessedClass: accessedClasses) {
						Tuple<String, String> tuple = new Tuple<String, String>(m.getName(), accessedClass.getName());
						tuples.add(tuple);
					}	
				}
			}
		}
		return tuples;
	}

	@Override
	public String getHeader() {
		return "Method;Class;FDP;NIM_D;NAA_D;DISTANCE_D;LOC_D;NMD_D;NAD_D;NIM_E;NAA_E;DISTANCE_E;LOC_E;NMD_E;NAD_E";
	}

	@Override
	public List<String> getMetricValues(Tuple<String, String> tuple) {
		MethodObject method = SystemObject.getInstance().getMethodByName(tuple.x);
		ClassObject enviedClass = SystemObject.getInstance().getClassByName(tuple.y);
		
		if (method == null || enviedClass == null) {
			return Arrays.asList("0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0");
		}
		
		ClassObject declaringClass = method.getDeclaringClass();
		
		List<String> metricValues = new ArrayList<String>();
		metricValues.add(String.valueOf(FDP.compute(method)));
		metricValues.add(String.valueOf(NIM.compute(method, declaringClass)));
		metricValues.add(String.valueOf(NAA.compute(method, declaringClass)));
		metricValues.add(String.format(Locale.US, "%.4f", DISTANCE.compute(method, declaringClass)));
		metricValues.add(String.valueOf(LOC.compute(declaringClass)));
		metricValues.add(String.valueOf(NMD.compute(declaringClass)));
		metricValues.add(String.valueOf(NAD.compute(declaringClass)));
		metricValues.add(String.valueOf(NIM.compute(method, enviedClass)));
		metricValues.add(String.valueOf(NAA.compute(method, enviedClass)));
		metricValues.add(String.format(Locale.US, "%.4f", DISTANCE.compute(method, enviedClass)));
		metricValues.add(String.valueOf(LOC.compute(enviedClass)));
		metricValues.add(String.valueOf(NMD.compute(enviedClass)));
		metricValues.add(String.valueOf(NAD.compute(enviedClass)));
		return metricValues;
	}
}
