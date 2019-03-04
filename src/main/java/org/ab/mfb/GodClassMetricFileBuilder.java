package org.ab.mfb;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class GodClassMetricFileBuilder implements IMetricFileBuilder {
	
	@Override
	public boolean buildMetricFile(String filePath) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(filePath);
		
		String header = "Class;LOC;NMD;NAD;LCOM5;NADC;ATFD;WMC";
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
			for (TopLevelClassObject c : f.getTopLevelClasses()) {
				StringBuffer lineBuffer = new StringBuffer();
				lineBuffer.append(c.getName());
				lineBuffer.append(";");
				lineBuffer.append(String.valueOf(LOC.compute(c)));
				lineBuffer.append(";");
				lineBuffer.append(String.valueOf(NMD.compute(c)));
				lineBuffer.append(";");
				lineBuffer.append(String.valueOf(NAD.compute(c)));
				lineBuffer.append(";");
				lineBuffer.append(String.valueOf(LCOM5.compute(c)));
				lineBuffer.append(";");
				lineBuffer.append(String.valueOf(NADC.compute(c)));
				lineBuffer.append(";");
				lineBuffer.append(String.valueOf(ATFD.compute(c)));
				lineBuffer.append(";");
				lineBuffer.append(String.valueOf(WMC.compute(c)));
				lines.add(lineBuffer.toString());
			}
		}
		
		return lines;
	}
}
