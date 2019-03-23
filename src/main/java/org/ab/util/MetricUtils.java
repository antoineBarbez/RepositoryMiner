package org.ab.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetricUtils {
	/**
	 * @param componentName: name of a class component, i.e., inner class, method or attribute.
	 * @return null if the argument name contains no dots or if the identifier
	 * of the class to be returned does not start with an uppercase letter.
	 * Examples;
	 * getDeclaringClassName("azertyuiop")                    = null
	 * getDeclaringClassName("getLabel()")                    = null
	 * getDeclaringClassName("org.package.AClass.getLabel()") = "org.package.AClass"
	 * getDeclaringClassName("org.package.aClass.getLabel()") = null
	 */
	public static String getDeclaringClassName(String componentName) {
		String[] splittedComponentName = componentName.split("\\.");
		if (splittedComponentName.length <= 1) {
			return null;
		}
		String[] splittedDeclaringClassName = Arrays.copyOf(splittedComponentName, splittedComponentName.length-1);
		String classIdentifier = splittedDeclaringClassName[splittedDeclaringClassName.length-1];
		if (!classIdentifier.isEmpty() && !Character.isUpperCase(classIdentifier.charAt(0))) {
			return null;
		}
		return String.join(".", splittedDeclaringClassName);
	}
	
	/**
	 * Returns the name of the field accessed by a method if this method 
	 * has an accessor-like name. 
	 * @param methodName
	 * @return null if the argument method does not have an accessor-like name.
	 * Examples:
	 * getAccessedField("azertyuiop")                                  = null
	 * getAccessedField("getLabel()")                                  = ".label"
	 * getAccessedField("org.package.AClass.getLabel()")               = "org.package.AClass.label"
	 * getAccessedField("org.package.AClass.getLabel(String)")         = null
	 * getAccessedField("org.package.AClass.isBlue()")                 = "org.package.AClass.blue"
	 * getAccessedField("org.package.AClass.setLabel()")               = null
	 * getAccessedField("org.package.AClass.setLabel(String)")         = "org.package.AClass.label"
	 * getAccessedField("org.package.AClass.setLabel(String, String)") = null
	 */
	public static String getAccessedFieldName(String methodName) {
		String[] splittedMethodName = methodName.split("\\.");
		String signature = splittedMethodName[splittedMethodName.length-1];
		Pattern p = Pattern.compile("(?:get|is)(\\w+)\\(\\)|set(\\w+)\\(\\w+\\)");
		Matcher m = p.matcher(signature) ;
		if (m.find()) {
			String fieldIdentifier = m.group(1) != null ? m.group(1) : m.group(2);
			fieldIdentifier = Character.toString(Character.toLowerCase(fieldIdentifier.charAt(0))) + fieldIdentifier.substring(1);
			String[] splittedClassName = Arrays.copyOf(splittedMethodName, splittedMethodName.length-1);
			String className = String.join(".", splittedClassName);
			return className + "." + fieldIdentifier;
		}
		return null;
	}
}
