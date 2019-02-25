package org.ab.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;

public class MethodObject extends CodeComponent {
	private boolean constructor = false;
	private Block body = null;
	private ClassObject declaringClass = null;
	private List<String> parameters = new ArrayList<String>();
	private Set<String> accessedFields = new HashSet<String>();
	private Set<String> invokedMethods = new HashSet<String>();
	
	public MethodObject(String identifier) {
		super(identifier);
	}
	
	public void addAccessedField(String field) {
		accessedFields.add(field);
	}
	
	public void addInvokedMethod(String method) {
		invokedMethods.add(method);
	}
	
	public Set<String> getAccessedFields () {
		return accessedFields;
	}
	
	public Block getBody() {
		return body;
	}
	
	public ClassObject getDeclaringClass() {
		return declaringClass;
	}
	
	public Set<String> getInvokedMethods() {
		return invokedMethods;
	}
	
	public String getName() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(declaringClass.getName() + "." + getIdentifier());
		buffer.append("(");
		buffer.append(String.join(", ", parameters));
		buffer.append(")");
		
		return buffer.toString();
	}
	
	public List<String> getParameters() {
		return parameters;
	}
	
	public boolean isAccessor() {
		return (isGetter() || isSetter());
	}
	
	public boolean isConstructor() {
		return constructor;
	}
	
	private boolean isGetter() {
    	if(body != null) {
	    	if(body.statements().size() == 1) {
	    		Statement statement = (Statement)body.statements().get(0);
	    		if(statement instanceof ReturnStatement) {
	    			ReturnStatement returnStatement = (ReturnStatement) statement;
	    			if((returnStatement.getExpression() instanceof SimpleName || returnStatement.getExpression() instanceof FieldAccess) 
	    					&& parameters.size() == 0 && accessedFields.size() == 1) {
	    				return true;
	    			}
	    		}
	    	}
    	}
    	return false;
    }
	
	private boolean isSetter() {
    	if(body != null) {
	    	if(body.statements().size() == 1) {
	    		Statement statement = (Statement)body.statements().get(0);
	    		if(statement instanceof ExpressionStatement) {
	    			ExpressionStatement expressionStatement = (ExpressionStatement)statement;
	    			if(expressionStatement.getExpression() instanceof Assignment  && parameters.size() == 1 && accessedFields.size() == 1) {
	    				Assignment assignment = (Assignment)expressionStatement.getExpression();
	    				if((assignment.getLeftHandSide() instanceof SimpleName || assignment.getLeftHandSide() instanceof FieldAccess) 
	    						&& assignment.getRightHandSide() instanceof SimpleName)
	    					return true;
	    			}
	    		}
	    	}
    	}
    	return false;
    }
	
	public void setBody(Block body) {
		this.body = body;
	}
	
	public void setConstructor(boolean constructor) {
		this.constructor = constructor;
	}
	
	public void setDeclaringClass(ClassObject declaringClass) {
		this.declaringClass = declaringClass;
	}
	
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}
}
