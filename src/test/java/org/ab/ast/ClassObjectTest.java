package org.ab.ast;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClassObjectTest {
	
	@Test
	public void inheritFromTest_NoInheritance() {
		SystemObject s = SystemObject.getInstance();
		
		ClassObject c1 = s.getClassByName("android.support.v4.content.ContextCompat");
		for (ClassObject c2: s.getClasses()) {
			assertFalse(c1.inheritFrom(c2));
		}
	}
	
	@Test
	public void inheritFromTest_OneLevelOfInheritance() {
		SystemObject s = SystemObject.getInstance();
		
		ClassObject c1 = s.getClassByName("android.support.v4.app.ActivityCompat");
		ClassObject c2 = s.getClassByName("android.support.v4.content.ContextCompat");
		assertTrue(c1.inheritFrom(c2));
		
		for (ClassObject c3: s.getClasses()) {
			if (!c3.getName().equals(c2.getName())) {
				assertFalse(c1.inheritFrom(c3));
			}
		}
	}
	
	@Test
	public void inheritFromTest_TwoLevelsOfInheritance() {
		SystemObject s = SystemObject.getInstance();
		
		ClassObject c1 = s.getClassByName("android.support.v4.content.CursorLoader");
		ClassObject c2 = s.getClassByName("android.support.v4.content.AsyncTaskLoader");
		ClassObject c3 = s.getClassByName("android.support.v4.content.Loader");
		assertTrue(c1.inheritFrom(c2));
		assertTrue(c2.inheritFrom(c3));
		assertTrue(c1.inheritFrom(c3));
		
	}

}
