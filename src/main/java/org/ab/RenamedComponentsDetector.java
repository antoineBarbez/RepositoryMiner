package org.ab;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.CodeComponent;
import org.ab.ast.FileObject;
import org.ab.ast.InnerClassObject;
import org.ab.ast.MethodObject;
import org.ab.ast.TopLevelClassObject;

public class RenamedComponentsDetector {
	private Map<String, String> renamedClasses;
	private Map<String, String> renamedMethods;
	
	public RenamedComponentsDetector() {
		renamedClasses = new HashMap<String, String>();
		renamedMethods = new HashMap<String, String>();
	}
	
	public void clear() {
		renamedClasses.clear();
		renamedMethods.clear();
	}
	
	private boolean compare(ClassObject c1, ClassObject c2) {
		// TODO
		return false;
	}
	
	private boolean compare(MethodObject m1, MethodObject m2) {
		// TODO
		return false;
	}
	
	public void detectRenamedComponents(FileObject initialFile, FileObject finalFile) {
		// Process top-level classes
		Set<TopLevelClassObject> initialTopLevelClasses = new HashSet<TopLevelClassObject>(initialFile.getTopLevelClasses());
		Set<TopLevelClassObject> finalTopLevelClasses = new HashSet<TopLevelClassObject>(finalFile.getTopLevelClasses());
		
		for (TopLevelClassObject ic : initialFile.getTopLevelClasses()) {
			TopLevelClassObject homonymClass;
			if ((homonymClass = (TopLevelClassObject)findComponentWithSameIdentifier(ic, finalTopLevelClasses)) != null) {
				if (!ic.getName().equals(homonymClass.getName())) {
					renamedClasses.put(ic.getName(), homonymClass.getName());
				}
				detectRenamedComponents(ic, homonymClass);
				initialTopLevelClasses.remove(ic);
				finalTopLevelClasses.remove(homonymClass);
			}
		}
		
		// Check for similarity between remaining classes
		for (TopLevelClassObject ic : initialTopLevelClasses) {
			TopLevelClassObject similarClass;
			if ((similarClass = (TopLevelClassObject)findSimilarClass(ic, finalTopLevelClasses)) != null) {
				renamedClasses.put(ic.getName(), similarClass.getName());
				detectRenamedComponents(ic, similarClass);
				finalTopLevelClasses.remove(similarClass);
			}
		}
	}
	
	private void detectRenamedComponents(ClassObject initialClass, ClassObject finalClass) {
		// Process inner classes
		Set<InnerClassObject> initialInnerClasses = new HashSet<InnerClassObject>(initialClass.getInnerClasses());
		Set<InnerClassObject> finalInnerClasses = new HashSet<InnerClassObject>(finalClass.getInnerClasses());
		
		for (InnerClassObject ic : initialClass.getInnerClasses()) {
			InnerClassObject homonymClass;
			if ((homonymClass = (InnerClassObject)findComponentWithSameIdentifier(ic, finalInnerClasses)) != null) {
				if (!ic.getName().equals(homonymClass.getName())) {
					renamedClasses.put(ic.getName(), homonymClass.getName());
				}
				detectRenamedComponents(ic, homonymClass);
				initialInnerClasses.remove(ic);
				finalInnerClasses.remove(homonymClass);
			}
		}
		
		// Check for similarity between remaining classes
		for (InnerClassObject ic: initialInnerClasses) {
			InnerClassObject similarClass;
			if ((similarClass = (InnerClassObject)findSimilarClass(ic, finalInnerClasses)) != null) {
				renamedClasses.put(ic.getName(), similarClass.getName());
				detectRenamedComponents(ic, similarClass);
				finalInnerClasses.remove(similarClass);
			}
		}
		
		// Process methods
		Set<MethodObject> initialMethods = new HashSet<MethodObject>(initialClass.getMethods());
		Set<MethodObject> finalMethods = new HashSet<MethodObject>(finalClass.getMethods());
		
		for (MethodObject im : initialClass.getMethods()) {
			MethodObject homonymMethod;
			if ((homonymMethod = (MethodObject)findComponentWithSameIdentifier(im, finalMethods)) !=null) {
				if (!im.getName().equals(homonymMethod.getName())) {
					renamedMethods.put(im.getName(), homonymMethod.getName());
				}
				initialMethods.remove(im);
				finalMethods.remove(homonymMethod);
			}
		}
		
		// Check for similarity between remaining methods
		for (MethodObject im : initialMethods) {
			MethodObject similarMethod;
			if ((similarMethod = findSimilarMethod(im, finalMethods)) != null) {
				renamedMethods.put(im.getName(), similarMethod.getName());
				finalMethods.remove(similarMethod);
			}
		}	
	}
	
	private CodeComponent findComponentWithSameIdentifier(CodeComponent cc, Set<? extends CodeComponent> ccs) {
		String identifier = cc.getIdentifier();
		for (CodeComponent cci : ccs) {
			if (cci.getIdentifier().equals(identifier)) {
				return cci;
			}
		}
		return null;
	}
	
	private ClassObject findSimilarClass(ClassObject c, Set<? extends ClassObject> cs) {
		for (ClassObject ci : cs) {
			if (compare(c, ci)) {
				return ci;
			}
		}
		return null;
	}
	
	private MethodObject findSimilarMethod(MethodObject m, Set<MethodObject> ms) {
		for (MethodObject mi : ms) {
			if (compare(m, mi)) {
				return mi;
			}
		}
		return null;
	}
	
	public Map<String, String> getRenamedClasses() {
		return renamedClasses;
	}
	
	public Map<String, String> getRenamedMethods() {
		return renamedMethods;
	}
}
