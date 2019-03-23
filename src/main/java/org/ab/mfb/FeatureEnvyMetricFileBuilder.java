package org.ab.mfb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

public class FeatureEnvyMetricFileBuilder extends MetricFileBuilder {

	@Override
	public List<String> getEntities() {
		SystemObject s = SystemObject.getInstance();
		
		List<String> entities = new ArrayList<String>();
		for (MethodObject m: s.getMethods()) {
			if (!m.getModifiers().contains("static") && !m.isAccessor() && !m.isConstructor()) {
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
						StringBuffer lineBuffer = new StringBuffer();
						lineBuffer.append(m.getName());
						lineBuffer.append(";");
						lineBuffer.append(accessedClass.getName());
						entities.add(lineBuffer.toString());
					}	
				}
			}
		}
		return entities;
	}

	@Override
	public String getHeader() {
		return "Method;Class;FDP;NIM_D;NAA_D;DISTANCE_D;LOC_D;NMD_D;NAD_D;NIM_E;NAA_E;DISTANCE_E;LOC_E;NMD_E;NAD_E";
	}

	@Override
	public List<String> getMetricValues(String entity) {
		String methodName = entity.split(";")[0];
		String className = entity.split(";")[1];
		
		MethodObject method = SystemObject.getInstance().getMethodByName(methodName);
		ClassObject enviedClass = SystemObject.getInstance().getClassByName(className);
		
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
	
	@Override
	public void handleRenamedComponents(Map<String, String> renamedClasses, Map<String, String> renamedMethods) {
		for (Map.Entry<String, String> entry_e : currentNames.entrySet()) {
			String initialName = entry_e.getKey();
			String currentName = entry_e.getValue();
			
			for (Map.Entry<String, String> entry_c : renamedClasses.entrySet()) {
				String oldClassName = entry_c.getKey();
				String newClassName = entry_c.getValue();
				if (initialName.endsWith(";" + oldClassName) || currentName.endsWith(";" + oldClassName)) {
					String currentMethodName = currentName.split(";")[0];
					currentNames.put(initialName, currentMethodName + ";" + newClassName);
				}
			}
			
			for (Map.Entry<String, String> entry_m : renamedMethods.entrySet()) {
				String oldMethodName = entry_m.getKey();
				String newMethodName = entry_m.getValue();
				if (initialName.startsWith(oldMethodName + ";") || currentName.startsWith(oldMethodName + ";")) {
					String currentClassName = currentName.split(";")[1];
					currentNames.put(initialName, newMethodName + ";" + currentClassName);
				}
			}
		}
	}
}
