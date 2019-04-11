package org.ab.mfb;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class UnaryMetricFileBuilder implements IMetricFileBuilder {
	private String previousFileContent = null;
	private Map<String, String> nameMap = new HashMap<String, String>();
	
	@Override
	public boolean buildMetricFile(String filePath) throws FileNotFoundException {
		if (nameMap.isEmpty()) {
			for (String name : getComponents()) {
				nameMap.put(name, name);
			}
		}
			
		StringBuffer fileBuffer = new StringBuffer();
		for (Map.Entry<String, String> entry : nameMap.entrySet()) {
			String initialName = entry.getKey();
			String currentName = entry.getValue();
			
			StringBuffer lineBuffer = new StringBuffer();
			lineBuffer.append(initialName);
			lineBuffer.append(";");
			Iterator<String> iteratorOnValues = getMetricValues(currentName).iterator();
			while (iteratorOnValues.hasNext()) {
				lineBuffer.append(iteratorOnValues.next());
				if (iteratorOnValues.hasNext()) {
					lineBuffer.append(";");
				}
			}
			
			fileBuffer.append(lineBuffer.toString());
			fileBuffer.append("\n");
		}
		
		if (!fileBuffer.toString().equals(previousFileContent)) {
			try (PrintWriter out = new PrintWriter(filePath)) {
				out.println(getHeader());
				out.print(fileBuffer.toString());
				previousFileContent = fileBuffer.toString();
				return true;
			}
		}
		return false;
	}
	
	public abstract List<String> getComponents();
	
	public abstract String getHeader();
	
	public abstract List<String> getMetricValues(String name);
	
	@Override
	public  void handleRenamedComponents(Map<String, String> renamedComponents) {
		for (Map.Entry<String, String> entry_1 : nameMap.entrySet()) {
			String initialName = entry_1.getKey();
			String currentName = entry_1.getValue();
			
			for (Map.Entry<String, String> entry_2 : renamedComponents.entrySet()) {
				String oldName = entry_2.getKey();
				String newName = entry_2.getValue();
				if (currentName.equals(oldName)) {
					nameMap.put(initialName, newName);
				}
			}
		}
	}
	
}
