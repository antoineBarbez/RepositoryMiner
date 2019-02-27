package org.ab.metrics;

import static org.junit.Assert.*;

import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;
import org.junit.BeforeClass;
import org.junit.Test;

public class CYCLOTest {
	private static MethodObject m1;
	private static MethodObject m2;
	private static MethodObject m3;
	private static MethodObject m4;
	private static MethodObject m5;
	private static MethodObject m6;
	private static MethodObject m7;
	
	@BeforeClass
	public static void setup() {
		SystemObject s = SystemObject.getInstance();
		m1 = s.getMethodByName("android.support.v4.app.FragmentManagerImpl.retainNonConfig()");
		m2 = s.getMethodByName("android.support.v4.app.FragmentManagerImpl.setBackStackIndex(int, BackStackRecord)");
		m3 = s.getMethodByName("android.support.v4.app.FragmentManagerImpl.allocBackStackIndex(BackStackRecord)");
		m4 = s.getMethodByName("android.support.v4.app.TaskStackBuilder.addParentStack(Activity)");
		m5 = s.getMethodByName("android.support.v4.content.ModernAsyncTask.executeOnExecutor(Executor, Params)");
		m6 = s.getMethodByName("android.support.v4.app.BackStackRecord.getName()");
		m7 = s.getMethodByName("android.support.v4.os.ParcelableCompatCreatorCallbacks.createFromParcel(Parcel, ClassLoader)");
	}
	
	@Test
	public void nonNullTest() {
		assertTrue(m1 != null);
		assertTrue(m2 != null);
		assertTrue(m3 != null);
		assertTrue(m4 != null);
		assertTrue(m5 != null);
		assertTrue(m6 != null);
		assertTrue(m7 != null);
		
	}

	@Test
	public void testM1() {
		// Multiple if with &&, for and conditional expression
		assertEquals(8, CYCLO.compute(m1));
	}
	
	@Test
	public void testM2() {
		// Multiple if , while
		assertEquals(8, CYCLO.compute(m2));
	}
	
	@Test
	public void testM3() {
		// Multiple if with ||
		assertEquals(6, CYCLO.compute(m3));
	}

	@Test
	public void testM4() {
		// while and catch
		assertEquals(3, CYCLO.compute(m4));
	}
	
	@Test
	public void testM5() {
		// if and switch
		assertEquals(4, CYCLO.compute(m5));
	}
	
	@Test
	public void testM6() {
		// simple accessor method
		assertEquals(1, CYCLO.compute(m6));
	}
	
	@Test
	public void testM7() {
		// Interface method
		assertEquals(1, CYCLO.compute(m7));
	}
}
