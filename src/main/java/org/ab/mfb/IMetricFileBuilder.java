package org.ab.mfb;

import java.io.FileNotFoundException;
import java.util.Map;

import org.ab.ast.CodeComponent;

public interface IMetricFileBuilder {
	
	public boolean buildMetricFile(String filePath) throws FileNotFoundException;
	
	public void handleRenamedComponents(Map<String, String> renamedComponents);
}
