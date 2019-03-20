package org.ab.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MetricUtilsTest {

	@Test
	public void getDeclaringClassNameTest() {
		assertNull(MetricUtils.getDeclaringClassName(""));
		assertNull(MetricUtils.getDeclaringClassName("azertyuiop"));
		assertNull(MetricUtils.getDeclaringClassName("getLabel()"));
		assertNull(MetricUtils.getDeclaringClassName("org.package.aClass.getLabel()"));
		assertEquals("org.package.AClass", MetricUtils.getDeclaringClassName("org.package.AClass.getLabel()"));
	}
	
	@Test
	public void getAccessedFieldNameTest() {
		assertNull(MetricUtils.getAccessedFieldName(""));
		assertNull(MetricUtils.getAccessedFieldName("azertyuiop"));
		assertEquals(".label", MetricUtils.getAccessedFieldName("getLabel()"));
		assertEquals("org.package.AClass.label", MetricUtils.getAccessedFieldName("org.package.AClass.getLabel()"));
		assertNull(MetricUtils.getAccessedFieldName("org.package.AClass.getLabel(String)"));
		assertEquals("org.package.AClass.blue", MetricUtils.getAccessedFieldName("org.package.AClass.isBlue()"));
		assertNull(MetricUtils.getAccessedFieldName("org.package.AClass.setLabel()"));
		assertEquals("org.package.AClass.label", MetricUtils.getAccessedFieldName("org.package.AClass.setLabel(String)"));
		assertNull(MetricUtils.getAccessedFieldName("org.package.AClass.setLabel(String, String)"));
	}

}
