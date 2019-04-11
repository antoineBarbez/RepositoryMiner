package org.ab.mfb.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.ab.ast.ClassObject;
import org.ab.ast.FileObject;
import org.ab.ast.SystemObject;
import org.ab.metrics.ATFD;
import org.ab.metrics.LCOM5;
import org.ab.metrics.LOC;
import org.ab.metrics.NAD;
import org.ab.metrics.NADC;
import org.ab.metrics.NMD;
import org.ab.metrics.WMC;
import org.ab.mfb.UnaryMetricFileBuilder;

public class GodClassMetricFileBuilder extends UnaryMetricFileBuilder {
	
	@Override
	public List<String> getComponents() {
		SystemObject s = SystemObject.getInstance();
		
		List<String> entities = new ArrayList<String>();
		for (FileObject f: s.getFiles()) {
			for (ClassObject c : f.getTopLevelClasses()) {
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
	public List<String> getMetricValues(String name) {
		ClassObject c = SystemObject.getInstance().getClassByName(name);
		
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
}
