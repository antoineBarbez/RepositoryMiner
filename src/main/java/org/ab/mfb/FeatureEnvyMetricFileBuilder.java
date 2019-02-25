package org.ab.mfb;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.FieldObject;
import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;
import org.ab.metrics.DISTANCE;
import org.ab.metrics.NAA;
import org.ab.metrics.NIM;

public class FeatureEnvyMetricFileBuilder implements IMetricFileBuilder {
	
	@Override
	public boolean buildMetricFile(String filePath) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(filePath);
		
		String header = "Method;Class;Distance;NIM;NAA";
		out.println(header);

		Iterator<String> iter = getLines().iterator();
		while (iter.hasNext()) {
			String csvLine = iter.next();
			if (csvLine != null) {
				out.println(csvLine);
			}
		}
		out.close();
		
		return true;
	}
	
	private List<String> getLines() {
		SystemObject s = SystemObject.getInstance();
		
		List<String> lines = new ArrayList<String>();
		for (MethodObject m: s.getMethods()) {
			if (!m.getModifiers().contains("static") && !m.isAccessor()) {
				Set<ClassObject> accessedClasses = new HashSet<ClassObject>();
				for (String accessedFieldName: m.getAccessedFields()) {
					FieldObject accessedField = s.getFieldByName(accessedFieldName);
					if (accessedField != null && !accessedField.getDeclaringClass().equals(m.getDeclaringClass())) {
						accessedClasses.add(accessedField.getDeclaringClass());
					}
				}
				
				for (String invokedMethodName: m.getInvokedMethods()) {
					MethodObject invokedMethod = s.getMethodByName(invokedMethodName);
					if (invokedMethod != null && !invokedMethod.getDeclaringClass().equals(m.getDeclaringClass())) {
						accessedClasses.add(invokedMethod.getDeclaringClass());
					}
				}
				
				for (ClassObject accessedClass: accessedClasses) {
					StringBuffer lineBuffer = new StringBuffer();
					lineBuffer.append(m.getName());
					lineBuffer.append(";");
					lineBuffer.append(accessedClass.getName());
					lineBuffer.append(";");
					lineBuffer.append(String.valueOf(DISTANCE.compute(m, accessedClass)));
					lineBuffer.append(";");
					lineBuffer.append(String.valueOf(NIM.compute(m, accessedClass)));
					lineBuffer.append(";");
					lineBuffer.append(String.valueOf(NAA.compute(m, accessedClass)));
					lines.add(lineBuffer.toString());
				}	
			}
		}
		
		return lines;
	}
}
