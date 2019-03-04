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
import org.ab.metrics.FDP;
import org.ab.metrics.LOC;
import org.ab.metrics.NAA;
import org.ab.metrics.NAD;
import org.ab.metrics.NIM;
import org.ab.metrics.NMD;

public class FeatureEnvyMetricFileBuilder implements IMetricFileBuilder {
	
	@Override
	public boolean buildMetricFile(String filePath) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(filePath);
		
		String header = "Method;Class;FDP;NIM_D;NAA_D;DISTANCE_D;LOC_D;NMD_D;NAD_D;NIM_E;NAA_E;DISTANCE_E;LOC_E;NMD_E;NAD_E";
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
					String fdp = String.valueOf(FDP.compute(m));
					String nim_d = String.valueOf(NIM.compute(m, m.getDeclaringClass()));
					String naa_d = String.valueOf(NAA.compute(m, m.getDeclaringClass()));
					String distance_d = String.valueOf(DISTANCE.compute(m, m.getDeclaringClass()));
					String loc_d = String.valueOf(LOC.compute(m.getDeclaringClass()));
					String nmd_d = String.valueOf(NMD.compute(m.getDeclaringClass()));
					String nad_d = String.valueOf(NAD.compute(m.getDeclaringClass()));
					
					for (ClassObject accessedClass: accessedClasses) {
						StringBuffer lineBuffer = new StringBuffer();
						lineBuffer.append(m.getName());
						lineBuffer.append(";");
						lineBuffer.append(accessedClass.getName());
						lineBuffer.append(";");
						lineBuffer.append(fdp);
						lineBuffer.append(";");
						lineBuffer.append(nim_d);
						lineBuffer.append(";");
						lineBuffer.append(naa_d);
						lineBuffer.append(";");
						lineBuffer.append(distance_d);
						lineBuffer.append(";");
						lineBuffer.append(loc_d);
						lineBuffer.append(";");
						lineBuffer.append(nmd_d);
						lineBuffer.append(";");
						lineBuffer.append(nad_d);
						lineBuffer.append(";");
						lineBuffer.append(String.valueOf(NIM.compute(m, accessedClass)));
						lineBuffer.append(";");
						lineBuffer.append(String.valueOf(NAA.compute(m, accessedClass)));
						lineBuffer.append(";");
						lineBuffer.append(String.valueOf(DISTANCE.compute(m, accessedClass)));
						lineBuffer.append(";");
						lineBuffer.append(String.valueOf(LOC.compute(accessedClass)));
						lineBuffer.append(";");
						lineBuffer.append(String.valueOf(NMD.compute(accessedClass)));
						lineBuffer.append(";");
						lineBuffer.append(String.valueOf(NAD.compute(accessedClass)));
						lines.add(lineBuffer.toString());
					}	
				}
			}
		}
		
		return lines;
	}
}
