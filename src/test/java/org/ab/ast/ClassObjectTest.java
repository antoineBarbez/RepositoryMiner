package org.ab.ast;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

public class ClassObjectTest {
	private static ClassObject interfaceClass;
	private static ClassObject abstractClass;
	private static ClassObject innerClass;
	private static ClassObject publicTopLevelClass;
	private static ClassObject nonPublicTopLevelClass;
	private static ClassObject parameterizedClass;
	
	@BeforeClass
	public static void setup() {
		SystemObject s = SystemObject.getInstance();
		interfaceClass = s.getClassByName("android.support.v4.os.ParcelableCompatCreatorCallbacks");
		abstractClass = s.getClassByName("android.support.v4.app.LoaderManager");
		innerClass = s.getClassByName("android.support.v4.app.Fragment.SavedState");
		publicTopLevelClass = s.getClassByName("android.support.v4.app.Fragment");
		nonPublicTopLevelClass = s.getClassByName("android.support.v4.app.LoaderManagerImpl");
		parameterizedClass = s.getClassByName("android.support.v4.content.Loader");
	}
	
	@Test
	public void nonNullTest() {
		assertTrue(interfaceClass != null);
		assertTrue(abstractClass != null);
		assertTrue(innerClass != null);
		assertTrue(publicTopLevelClass != null);
		assertTrue(nonPublicTopLevelClass != null);
		assertTrue(parameterizedClass != null);
	}
	
	@Test
	public void getFieldsTest() {
		assertEquals(0, interfaceClass.getFields().size());
		assertEquals(0, abstractClass.getFields().size());
		assertEquals(2, innerClass.getFields().size());
		assertEquals(46, publicTopLevelClass.getFields().size());
		assertEquals(9, nonPublicTopLevelClass.getFields().size());
		assertEquals(7, parameterizedClass.getFields().size());
	}
		
	@Test
	public void getInnerClassesTest() {
		assertEquals(0, interfaceClass.getInnerClasses().size());
		assertEquals(1, abstractClass.getInnerClasses().size());
		assertEquals(0, innerClass.getInnerClasses().size());
		assertEquals(2, publicTopLevelClass.getInnerClasses().size());
		assertEquals(1, nonPublicTopLevelClass.getInnerClasses().size());
		assertEquals(2, parameterizedClass.getInnerClasses().size());
	}
	
	@Test
	public void getMethodsTest() {
		assertEquals(2, interfaceClass.getMethods().size());
		assertEquals(7, abstractClass.getMethods().size());
		assertEquals(4, innerClass.getMethods().size());
		assertEquals(75, publicTopLevelClass.getMethods().size());
		assertEquals(19, nonPublicTopLevelClass.getMethods().size());
		assertEquals(24, parameterizedClass.getMethods().size());
	}
	
	@Test
	public void isInterfaceTest() {
		assertTrue(interfaceClass.isInterface());
		assertFalse(abstractClass.isInterface());
		assertFalse(innerClass.isInterface());
		assertFalse(publicTopLevelClass.isInterface());
		assertFalse(nonPublicTopLevelClass.isInterface());
		assertFalse(parameterizedClass.isInterface());
	}
	
	// InheritFrom Tests.
	
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
