package org.ab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ab.ast.ClassObject;
import org.ab.ast.FileObject;
import org.ab.ast.InnerClassObject;
import org.ab.ast.MethodObject;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.Statement;

public class RenamedComponentsDetector {
	private static final double MAX_NAME_DISTANCE = 0.4;
	private static final int MAX_PARAM_DISTANCE = 1;
	private static final double MIN_RATIO_MATCHING_STATEMENTS = 0.7;
	private static final double MIN_RATIO_MATCHING_METHODS = 0.7;
	private static final double MIN_RATIO_MATCHING_INNER_CLASSES = 0.5;
	
	private ASTMatcher matcher;
	
	private Map<String, String> renamedComponents;
	
	public RenamedComponentsDetector() {
		matcher = new ASTMatcher();
		renamedComponents = new HashMap<String, String>();
	}
	
	public void clear() {
		renamedComponents.clear();
	}
	
	private boolean compare(ClassObject c1, ClassObject c2) {
		double ratioMatchingInnerClasses;
		if (c1.getInnerClasses().isEmpty() && c2.getInnerClasses().isEmpty()) {
			ratioMatchingInnerClasses = 1.0;
		}else {
			int nbMatchingInnerClasses = 0;
			Set<InnerClassObject> nonMatchedInnerClassesInC1 = new HashSet<InnerClassObject>(c1.getInnerClasses());
			Set<InnerClassObject> nonMatchedInnerClassesInC2 = new HashSet<InnerClassObject>(c2.getInnerClasses());
			
			for (InnerClassObject ic1 : c1.getInnerClasses()) {
				InnerClassObject homonymClass;
				if ((homonymClass = (InnerClassObject)findClassWithSameIdentifier(ic1, nonMatchedInnerClassesInC2)) != null) {
					nonMatchedInnerClassesInC1.remove(ic1);
					nonMatchedInnerClassesInC2.remove(homonymClass);
					nbMatchingInnerClasses++;
				}
			}
			
			for (InnerClassObject ic1 : nonMatchedInnerClassesInC1) {
				InnerClassObject similarClass;
				if ((similarClass = (InnerClassObject)findSimilarClass(ic1, nonMatchedInnerClassesInC2)) != null) {
					nonMatchedInnerClassesInC2.remove(similarClass);
					nbMatchingInnerClasses++;
				}
			}
			ratioMatchingInnerClasses = (double)nbMatchingInnerClasses/(double)Math.max(c1.getInnerClasses().size(), c2.getInnerClasses().size());
		}
		
		double ratioMatchingMethods;
		if (c1.getMethods().isEmpty() && c2.getMethods().isEmpty()) {
			ratioMatchingMethods = 1.0;
		}else {
			int nbMatchingMethods = 0;
			Set<MethodObject> nonMatchedMethodsInC1 = new HashSet<MethodObject>(c1.getMethods());
			Set<MethodObject> nonMatchedMethodsInC2 = new HashSet<MethodObject>(c2.getMethods());
			
			for (MethodObject m1 : c1.getMethods()) {
				MethodObject homonymMethod;
				if ((homonymMethod = findMethodWithSameSignature(m1, nonMatchedMethodsInC2)) != null) {
					nonMatchedMethodsInC1.remove(m1);
					nonMatchedMethodsInC2.remove(homonymMethod);
					nbMatchingMethods++;
				}
			}
			
			for (MethodObject m1 : nonMatchedMethodsInC1) {
				MethodObject similarMethod;
				if ((similarMethod = findSimilarMethod(m1, nonMatchedMethodsInC2)) != null) {
					nonMatchedMethodsInC2.remove(similarMethod);
					nbMatchingMethods++;
				}
			}
			ratioMatchingMethods = (double)nbMatchingMethods/(double)Math.max(c1.getMethods().size(), c2.getMethods().size());
		}
		return ((ratioMatchingMethods >= MIN_RATIO_MATCHING_METHODS) && (ratioMatchingInnerClasses >= MIN_RATIO_MATCHING_INNER_CLASSES));
	}
	
	private boolean compare(MethodObject m1, MethodObject m2) {
		return (compareSignature(m1, m2) && compareBody(m1, m2));
	}
	
	private boolean compareBody(MethodObject m1, MethodObject m2) {
		if (m1.getBody() == null || m2.getBody() == null) {
			if (m1.getBody() == null && m2.getBody() == null) {
				return true;
			}
			return false;
		}
		
		if (m1.getBody().statements().isEmpty() || m2.getBody().statements().isEmpty()) {
			if (m1.getBody().statements().isEmpty() && m2.getBody().statements().isEmpty()) {
				return true;
			}
			return false;
		}
		
		int nbMatchingStatements = 0;
		List<Object> statementsM2 = new ArrayList<Object>(m2.getBody().statements());
		
		for (Object o1 : m1.getBody().statements()) {
			Statement statementM1 = (Statement)o1;
			Object isomorphicStatement;
			if ((isomorphicStatement = findIsomorphicStatement(statementM1, statementsM2)) != null) {
				statementsM2.remove(isomorphicStatement);
				nbMatchingStatements++;
			}
		}
		double ratioMatchingStatements = (double)nbMatchingStatements/(double)Math.max(m1.getBody().statements().size(), m2.getBody().statements().size());
		return (ratioMatchingStatements >= MIN_RATIO_MATCHING_STATEMENTS);
	}
	
	private boolean compareSignature(MethodObject m1, MethodObject m2) {
		if (m1.isConstructor() || m2.isConstructor()) {
			if (m1.isConstructor() && m2.isConstructor()) {
				return true;
			}
			return false;
		}
		
		if (!m1.getReturnType().equals(m2.getReturnType())) {
			return false;
		}
		
		boolean m1_getter = m1.isGetter();
		boolean m2_getter = m2.isGetter();
		if (m1_getter || m2_getter) {
			if (m1_getter && m2_getter) {
				return true;
			}
			return false;
		}
		
		boolean m1_setter = m1.isSetter();
		boolean m2_setter = m2.isSetter();
		if (m1_setter || m2_setter) {
			if (m1_setter && m2_setter) {
				return true;
			}
			return false;
		}
		
		if (m1.getParameters().equals(m2.getParameters())) {
			if (nameDistance(m1.getIdentifier(), m2.getIdentifier()) <= MAX_NAME_DISTANCE) {
				return true;
			}
			return false;
		}
		
		if (m1.getIdentifier().equals(m2.getIdentifier())) {
			if (paramDistance(m1.getParameters(), m2.getParameters()) <= MAX_PARAM_DISTANCE) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * @return Levenstein distance adapted for arrays.
	 */
	private int paramDistance(List<String> params1, List<String> params2) {
		Map<String, String> paramToCharMap = new HashMap<String, String>();
		int valueOfChar = 65;
		
		Set<String> allParameters = new HashSet<String>();
		allParameters.addAll(params1);
		allParameters.addAll(params2);
		
		for (String param : allParameters) {
			paramToCharMap.put(param, Character.toString((char)valueOfChar));
			valueOfChar++;
		}
		
		String params1AsString = "";
		for (String param : params1) {
			params1AsString += paramToCharMap.get(param);
		}
		
		String params2AsString = "";
		for (String param : params2) {
			params2AsString += paramToCharMap.get(param);
		}
		
		LevenshteinDistance distance = new LevenshteinDistance();
		return distance.apply(params1AsString, params2AsString);
	}
	
	/**
	 * @return Normalized Levensthein distance between two strings.
	 */
	private double nameDistance(String name1, String name2) {
		if (name1.isEmpty() && name2.isEmpty()) {
			return 0;
		}
		
		LevenshteinDistance distance = new LevenshteinDistance();
		int value = distance.apply(name1.toLowerCase(), name2.toLowerCase());
		double normalizedValue = (double)value/(double)Math.max(name1.length(), name2.length());
		return normalizedValue;
	}
	
	public void detectRenamedComponents(FileObject initialFile, FileObject finalFile) {
		// Process top-level classes
		Set<ClassObject> initialTopLevelClasses = new HashSet<ClassObject>(initialFile.getTopLevelClasses());
		Set<ClassObject> finalTopLevelClasses = new HashSet<ClassObject>(finalFile.getTopLevelClasses());
		
		for (ClassObject ic : initialFile.getTopLevelClasses()) {
			ClassObject homonymClass;
			if ((homonymClass = (ClassObject)findClassWithSameIdentifier(ic, finalTopLevelClasses)) != null) {
				if (!ic.getName().equals(homonymClass.getName())) {
					renamedComponents.put(ic.getName(), homonymClass.getName());
				}
				detectRenamedComponents(ic, homonymClass);
				initialTopLevelClasses.remove(ic);
				finalTopLevelClasses.remove(homonymClass);
			}
		}
		
		// Check for similarity between remaining classes
		for (ClassObject ic : initialTopLevelClasses) {
			ClassObject similarClass;
			if ((similarClass = (ClassObject)findSimilarClass(ic, finalTopLevelClasses)) != null) {
				renamedComponents.put(ic.getName(), similarClass.getName());
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
			if ((homonymClass = (InnerClassObject)findClassWithSameIdentifier(ic, finalInnerClasses)) != null) {
				if (!ic.getName().equals(homonymClass.getName())) {
					renamedComponents.put(ic.getName(), homonymClass.getName());
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
				renamedComponents.put(ic.getName(), similarClass.getName());
				detectRenamedComponents(ic, similarClass);
				finalInnerClasses.remove(similarClass);
			}
		}
		
		// Process methods
		Set<MethodObject> initialMethods = new HashSet<MethodObject>(initialClass.getMethods());
		Set<MethodObject> finalMethods = new HashSet<MethodObject>(finalClass.getMethods());
		
		for (MethodObject im : initialClass.getMethods()) {
			MethodObject homonymMethod;
			if ((homonymMethod = findMethodWithSameSignature(im, finalMethods)) !=null) {
				if (!im.getName().equals(homonymMethod.getName())) {
					renamedComponents.put(im.getName(), homonymMethod.getName());
				}
				initialMethods.remove(im);
				finalMethods.remove(homonymMethod);
			}
		}
		
		// Check for similarity between remaining methods
		for (MethodObject im : initialMethods) {
			MethodObject similarMethod;
			if ((similarMethod = findSimilarMethod(im, finalMethods)) != null) {
				renamedComponents.put(im.getName(), similarMethod.getName());
				finalMethods.remove(similarMethod);
			}
		}	
	}
	
	private ClassObject findClassWithSameIdentifier(ClassObject c, Set<? extends ClassObject> classes) {
		String identifier = c.getIdentifier();
		for (ClassObject ci : classes) {
			if (ci.getIdentifier().equals(identifier)) {
				return ci;
			}
		}
		return null;
	}
	
	private Object findIsomorphicStatement(Statement s, List<Object> statements) {
		for (Object si : statements) {
			if (s.subtreeMatch(matcher, si)) {
				return si;
			}
		}
		return null;
	}
	
	private MethodObject findMethodWithSameSignature(MethodObject m, Set<MethodObject> methods) {
		String identifier = m.getIdentifier();
		for (MethodObject mi : methods) {
			if (mi.getIdentifier().equals(identifier) && mi.getParameters().equals(m.getParameters())) {
				return mi;
			}
		}
		return null;
	}
	
	private ClassObject findSimilarClass(ClassObject c, Set<? extends ClassObject> classes) {
		for (ClassObject ci : classes) {
			if (compare(c, ci)) {
				return ci;
			}
		}
		return null;
	}
	
	private MethodObject findSimilarMethod(MethodObject m, Set<MethodObject> methods) {
		for (MethodObject mi : methods) {
			if (compare(m, mi)) {
				return mi;
			}
		}
		return null;
	}
	
	public Map<String, String> getRenamedComponents() {
		return renamedComponents;
	}
}
