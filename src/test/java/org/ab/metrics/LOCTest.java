package org.ab.metrics;

import static org.junit.Assert.*;

import org.ab.ast.MethodObject;
import org.ab.ast.SystemObject;
import org.junit.BeforeClass;
import org.junit.Test;

public class LOCTest {
	private static MethodObject m1;
	private static MethodObject m2;
	private static MethodObject m3;
	private static MethodObject m4;
	private static MethodObject m5;
	private static MethodObject m6;
	private static MethodObject m7;
	private static MethodObject m8;
	
	@BeforeClass
	public static void setup() {
		SystemObject s = SystemObject.getInstance();
		m1 = s.getMethodByName("android.support.v4.app.LoaderManagerImpl.LoaderInfo.retain()");
		m2 = s.getMethodByName("android.support.v4.app.LoaderManagerImpl.finishRetain()");
		m3 = s.getMethodByName("android.support.v4.app.LoaderManagerImpl.LoaderInfo.stop()");
		m4 = s.getMethodByName("android.support.v4.app.LoaderManagerImpl.installLoader(LoaderInfo)");
		m5 = s.getMethodByName("android.support.v4.content.IntentCompat.IntentCompat()");
		m6 = s.getMethodByName("android.support.v4.app.LoaderManagerImpl.doStart()");
		m7 = s.getMethodByName("android.support.v4.app.NotificationCompat.InboxStyle.InboxStyle()");
		m8 = s.getMethodByName("android.support.v4.os.ParcelableCompatCreatorCallbacks.createFromParcel(Parcel, ClassLoader)");
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
		assertTrue(m8 != null);
	}
	
	@Test
	// No space, no comments
	public void testM1() {
		assertEquals(7, LOC.compute(m1));
	}
	
	@Test
	// Single blank line
	public void testM2() {
		assertEquals(9, LOC.compute(m2));
	}
	
	@Test
	// Single line comments with "//"
	public void testM3() {
		assertEquals(11, LOC.compute(m3));
	}

	@Test
	// Multi-lines comments with "//"
	public void testM4() {
		assertEquals(6, LOC.compute(m4));
	}
	
	@Test
	// Single line comments with "/**/"
	public void testM5() {
		assertEquals(2, LOC.compute(m5));
	}
	
	@Test
	// Blanck lines and comments
	public void testM6() {
		assertEquals(13, LOC.compute(m6));
	}
	
	@Test
	// Empty method
	public void testM7() {
		assertEquals(2, LOC.compute(m7));
	}
	
	@Test
	// Interface method
	public void testM8() {
		assertEquals(1, LOC.compute(m8));
	}

}
