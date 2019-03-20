package org.ab.ast;

import static org.junit.Assert.*;

import org.junit.Test;

public class MethodObjectTest {
	
	@Test
	public void accessedFieldsTest() {
		SystemObject s = SystemObject.getInstance();
		
		MethodObject m1 = s.getMethodByName("android.support.v4.content.CursorLoader.loadInBackground()");
		MethodObject m2 = s.getMethodByName("android.support.v4.content.CursorLoader.deliverResult(Cursor)");
		MethodObject m3 = s.getMethodByName("android.support.v4.content.AsyncTaskLoader.cancelLoad()");
		MethodObject m4 = s.getMethodByName("android.support.v4.app.FragmentManagerImpl.popBackStackState(Handler, String, int, int)");
		MethodObject m5 = s.getMethodByName("android.support.v4.app.TaskStackBuilder.addParentStack(Activity)");
		
		assertEquals(6, m1.getAccessedFields().size());
		assertEquals(1, m2.getAccessedFields().size());
		assertEquals(6, m3.getAccessedFields().size());
		assertEquals(5, m4.getAccessedFields().size());
		assertEquals(2, m5.getAccessedFields().size());
	}
	
	@Test
	public void getDeclaringClassTest() {
		SystemObject s = SystemObject.getInstance();
		
		// Standard method
		MethodObject m1 = s.getMethodByName("android.support.v4.content.CursorLoader.loadInBackground()");
		assertEquals("android.support.v4.content.CursorLoader", m1.getDeclaringClass().getName());
		assertTrue(s.getClassByName("android.support.v4.content.CursorLoader") == m1.getDeclaringClass());
		
		// Method of inner class
		MethodObject m2 = s.getMethodByName("android.support.v4.app.TaskStackBuilder.TaskStackBuilderImplBase.getPendingIntent(Context, Intent[], int, int, Bundle)");
		assertEquals("android.support.v4.app.TaskStackBuilder.TaskStackBuilderImplBase", m2.getDeclaringClass().getName());
		assertTrue(s.getClassByName("android.support.v4.app.TaskStackBuilder.TaskStackBuilderImplBase") == m2.getDeclaringClass());
		
		// Overridden Method
		MethodObject m3 = s.getMethodByName("android.support.v4.app.FragmentManagerImpl.popBackStack()");
		assertEquals("android.support.v4.app.FragmentManagerImpl", m3.getDeclaringClass().getName());
		assertTrue(s.getClassByName("android.support.v4.app.FragmentManagerImpl") == m3.getDeclaringClass());
	}
	
	@Test
	public void invokedMethodsTest() {
		SystemObject s = SystemObject.getInstance();
		
		MethodObject m1 = s.getMethodByName("android.support.v4.content.CursorLoader.loadInBackground()");
		MethodObject m2 = s.getMethodByName("android.support.v4.content.CursorLoader.deliverResult(Cursor)");
		MethodObject m3 = s.getMethodByName("android.support.v4.content.AsyncTaskLoader.cancelLoad()");
		MethodObject m4 = s.getMethodByName("android.support.v4.app.FragmentManagerImpl.popBackStackState(Handler, String, int, int)");
		MethodObject m5 = s.getMethodByName("android.support.v4.app.TaskStackBuilder.addParentStack(Activity)");
		
		assertEquals(2, m1.getInvokedMethods().size());
		assertEquals(3, m2.getInvokedMethods().size());
		assertEquals(1, m3.getInvokedMethods().size());
		assertEquals(3, m4.getInvokedMethods().size());
		assertEquals(2, m5.getInvokedMethods().size());
	}
	
	@Test
	public void isAccessorTest() {
		SystemObject s = SystemObject.getInstance();
		
		MethodObject nonAccessorMethod1 = s.getMethodByName("android.support.v4.app.Fragment.instantiate(Context, String, Bundle)");
		MethodObject nonAccessorMethod2 = s.getMethodByName("android.support.v4.app.Fragment.restoreViewState()");
		MethodObject nonAccessorMethod3 = s.getMethodByName("android.support.v4.app.Fragment.setIndex(int)");
		MethodObject nonAccessorMethod4 = s.getMethodByName("android.support.v4.app.Fragment.hashCode()");
		assertFalse(nonAccessorMethod1.isAccessor());
		assertFalse(nonAccessorMethod2.isAccessor());
		assertFalse(nonAccessorMethod3.isAccessor());
		assertFalse(nonAccessorMethod4.isAccessor());
		
		MethodObject getter1 = s.getMethodByName("android.support.v4.app.Fragment.getId()");
		MethodObject getter2 = s.getMethodByName("android.support.v4.app.Fragment.getArguments()");
		MethodObject getter3 = s.getMethodByName("android.support.v4.app.Fragment.isRemoving()");
		MethodObject getter4 = s.getMethodByName("android.support.v4.content.CursorLoader.getProjection()");
		assertTrue(getter1.isAccessor());
		assertTrue(getter2.isAccessor());
		assertTrue(getter3.isAccessor());
		assertTrue(getter4.isAccessor());
		
		MethodObject setter1 = s.getMethodByName("android.support.v4.app.Fragment.setRetainInstance(boolean)");
		MethodObject setter2 = s.getMethodByName("android.support.v4.content.CursorLoader.setSelection(String)");
		MethodObject setter3 = s.getMethodByName("android.support.v4.content.CursorLoader.setSelectionArgs(String[])");
		MethodObject setter4 = s.getMethodByName("android.support.v4.content.CursorLoader.setUri(Uri)");
		assertTrue(setter1.isAccessor());
		assertTrue(setter2.isAccessor());
		assertTrue(setter3.isAccessor());
		assertTrue(setter4.isAccessor());
	}
	
	@Test
	public void isConstructor() {
		SystemObject s = SystemObject.getInstance();
		
		MethodObject m1 = s.getMethodByName("android.support.v4.app.Fragment.SavedState.SavedState(Bundle)");
		MethodObject m2 = s.getMethodByName("android.support.v4.app.Fragment.SavedState.SavedState(Parcel, ClassLoader)");
		MethodObject m3 = s.getMethodByName("android.support.v4.app.Fragment.SavedState.writeToParcel(Parcel, int)");
		MethodObject m4 = s.getMethodByName("android.support.v4.app.Fragment.InstantiationException.InstantiationException(String, Exception)");
		MethodObject m5 = s.getMethodByName("android.support.v4.app.Fragment.Fragment()");
		MethodObject m6 = s.getMethodByName("android.support.v4.app.Fragment.getId()");
		MethodObject m7 = s.getMethodByName("android.support.v4.app.Fragment.setInitialSavedState(SavedState)");
		MethodObject m8 = s.getMethodByName("android.support.v4.app.Fragment.onCreateAnimation(int, boolean, int)");
		
		assertTrue(m1.isConstructor());
		assertTrue(m2.isConstructor());
		assertFalse(m3.isConstructor());
		assertTrue(m4.isConstructor());
		assertTrue(m5.isConstructor());
		assertFalse(m6.isConstructor());
		assertFalse(m7.isConstructor());
		assertFalse(m8.isConstructor());
	}
}
