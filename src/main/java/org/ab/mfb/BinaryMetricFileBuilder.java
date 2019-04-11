package org.ab.mfb;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BinaryMetricFileBuilder implements IMetricFileBuilder {
	private String previousFileContent = null;
	private Map<String, Tuple<String, String>> nameMap = new HashMap<String, Tuple<String, String>>();
	
	@Override
	public boolean buildMetricFile(String filePath) throws FileNotFoundException {
		if (nameMap.isEmpty()) {
			for (Tuple<String, String> tuple : getComponents()) {
				String name = tuple.x + ";" + tuple.y;
				nameMap.put(name, tuple);
			}
		}
			
		StringBuffer fileBuffer = new StringBuffer();
		for (Map.Entry<String, Tuple<String, String>> entry : nameMap.entrySet()) {
			String initialName = entry.getKey();
			Tuple<String, String> tuple = entry.getValue();
			
			StringBuffer lineBuffer = new StringBuffer();
			lineBuffer.append(initialName);
			lineBuffer.append(";");
			Iterator<String> iteratorOnValues = getMetricValues(tuple).iterator();
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
	
	public abstract List<Tuple<String, String>> getComponents();
	
	public abstract String getHeader();
	
	public abstract List<String> getMetricValues(Tuple<String, String> tuple);
	
	@Override
	public void handleRenamedComponents(Map<String, String> renamedComponents) {
		for (Map.Entry<String, Tuple<String, String>> entry_1 : nameMap.entrySet()) {
			String initialName = entry_1.getKey();
			String currentName1 = entry_1.getValue().x;
			String currentName2 = entry_1.getValue().y;
			
			for (Map.Entry<String, String> entry_2 : renamedComponents.entrySet()) {
				String oldName = entry_2.getKey();
				String newName = entry_2.getValue();
				if(currentName1.equals(oldName)) {
					Tuple<String, String> newTuple = new Tuple<String, String> (newName, currentName2);
					nameMap.put(initialName, newTuple);
					currentName1 = newName;
				}
				
				if(currentName2.equals(oldName)) {
					Tuple<String, String> newTuple = new Tuple<String, String> (currentName1, newName);
					nameMap.put(initialName, newTuple);
				}
			}
		}
	}
	
	public class Tuple<X, Y> { 
	    public final X x; 
	    public final Y y; 
	    public Tuple(X x, Y y) { 
	        this.x = x; 
	        this.y = y; 
	    }
	}
}

