package org.ab.mfb;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ab.ast.ClassObject;
import org.ab.ast.FileObject;
import org.ab.ast.SystemObject;
import org.ab.metrics.IUnaryClassMetric;
import org.ab.metrics.impl.LCOM5;
import org.ab.metrics.impl.LOC;
import org.ab.metrics.impl.NAD;
import org.ab.metrics.impl.NADC;
import org.ab.metrics.impl.NMD;

public class GodClassMetricFileBuilder implements IMetricFileBuilder {
	private List<IUnaryClassMetric> metrics;
	
	public GodClassMetricFileBuilder() {
		metrics = new ArrayList<IUnaryClassMetric>();
		
		metrics.add(new LOC());
		metrics.add(new NMD());
		metrics.add(new NAD());
		metrics.add(new LCOM5());
		metrics.add(new NADC());
	}
	
	@Override
	public boolean buildMetricFile(String filePath) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(filePath);
		
		String header = "Class;LOC;NMD;NAD;LCOM5;NADC";
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
		for (FileObject f: s.getFiles()) {
			for (ClassObject c : f.getClasses()) {
				StringBuffer lineBuffer = new StringBuffer();
				lineBuffer.append(c.getName());
				for (IUnaryClassMetric metric: metrics) {
					lineBuffer.append(";");
					lineBuffer.append(String.valueOf(metric.compute(c)));
				}
				lines.add(lineBuffer.toString());
			}
		}
		
		return lines;
	}
}
