package org.ab.mfb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ab.ast.ClassObject;
import org.ab.ast.FileObject;
import org.ab.ast.SystemObject;
import org.ab.ast.TopLevelClassObject;
import org.ab.metrics.ATFD;
import org.ab.metrics.LCOM5;
import org.ab.metrics.LOC;
import org.ab.metrics.NAD;
import org.ab.metrics.NADC;
import org.ab.metrics.NMD;
import org.ab.metrics.WMC;

public class GodClassMetricFileBuilder extends MetricFileBuilder {
	
	@Override
	public List<String> getEntities() {
		SystemObject s = SystemObject.getInstance();
		
		List<String> entities = new ArrayList<String>();
		for (FileObject f: s.getFiles()) {
			for (TopLevelClassObject c : f.getTopLevelClasses()) {
				entities.add(c.getName());
			}
		}
		return entities;
	}
	
	@Override
	public String getHeader() {
		return "Class;LOC;NMD;NAD;LCOM5;NADC;ATFD;WMC";
	}
	
	@Override
	public List<String> getMetricValues(String entity) {
		ClassObject c = SystemObject.getInstance().getClassByName(entity);
		
		if (c == null) {
			return Arrays.asList("0", "0", "0", "0", "0", "0", "0");
		}
		
		List<String> metricValues = new ArrayList<String>();
		metricValues.add(String.valueOf(LOC.compute(c)));
		metricValues.add(String.valueOf(NMD.compute(c)));
		metricValues.add(String.valueOf(NAD.compute(c)));
		metricValues.add(String.format(Locale.US, "%.4f", LCOM5.compute(c)));
		metricValues.add(String.valueOf(NADC.compute(c)));
		metricValues.add(String.valueOf(ATFD.compute(c)));
		metricValues.add(String.valueOf(WMC.compute(c)));
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
				if (initialName.equals(oldClassName) || currentName.equals(oldClassName)) {
					currentNames.put(initialName, newClassName);
				}
			}
		}
	}
}
