package org.ab.mfb;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class MetricFileBuilder {
	public Map<String, String> currentNames = null;
	
	public boolean buildMetricFile(String filePath) throws FileNotFoundException {
		if (currentNames == null) {
			currentNames = new HashMap<String, String>();
			for (String entityName : getEntities()) {
				currentNames.put(entityName, entityName);
			}
		}
		
		try (PrintWriter out = new PrintWriter(filePath)) {
			out.println(getHeader());

			Iterator<String> iteratorOnEntities = currentNames.keySet().iterator();
			while (iteratorOnEntities.hasNext()) {
				String entityName = iteratorOnEntities.next();
				String currentEntityName = currentNames.get(entityName);
				
				StringBuffer lineBuffer = new StringBuffer();
				lineBuffer.append(entityName);
				lineBuffer.append(";");
				Iterator<String> iteratorOnValues = getMetricValues(currentEntityName).iterator();
				while (iteratorOnValues.hasNext()) {
					lineBuffer.append(iteratorOnValues.next());
					if (iteratorOnValues.hasNext()) {
						lineBuffer.append(";");
					}
				}
				out.println(lineBuffer.toString());
			}
		}
		return true;
	}
	
	public abstract List<String> getEntities();
	
	public abstract String getHeader();
	
	public abstract List<String> getMetricValues(String entity);
	
	public abstract void handleRenamedComponents(Map<String, String> renamedClasses, Map<String, String> renamedMethods);
}
