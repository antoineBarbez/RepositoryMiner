package org.ab.mfb;

import java.io.FileNotFoundException;

public interface IMetricFileBuilder {
	public boolean buildMetricFile(String filePath) throws FileNotFoundException ;
}
