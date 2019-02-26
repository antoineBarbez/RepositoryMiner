package org.ab.metrics;

import org.ab.ast.MethodObject;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.WhileStatement;

/*
 * CYCLO: Cyclomatic complexity metric. 
 */

public class CYCLO {
	
	public static int compute(MethodObject m) {
		if (m.getBody() == null) {
			return 1;
		}
		
		CCVisitor visitor = new CCVisitor();
		m.getBody().accept(visitor);
		
		return visitor.getCc();
	}
}

class CCVisitor extends ASTVisitor{
	public int cc = 1;
	
	public int getCc() {
		return cc;
	}
	
	@Override 
	public boolean visit(CatchClause node) {
		cc += getComplexity(node.getException().toString(), node.getNodeType());
		return true;
	}
	
	@Override 
	public boolean visit(ConditionalExpression node) {
		cc += getComplexity(node.getExpression().toString(), node.getNodeType());
		return true;
	}
	
	@Override 
	public boolean visit(ForStatement node) {
		cc += getComplexity(node.getExpression().toString(), node.getNodeType());
		return true;
	}
	
	@Override
	public boolean visit(IfStatement node) {
		cc += getComplexity(node.getExpression().toString(), node.getNodeType());
		return true;
	}
	
	@Override 
	public boolean visit(SwitchCase node) {
		cc++;
		return true;
	}
	
	@Override 
	public boolean visit(WhileStatement node) {
		cc += getComplexity(node.getExpression().toString(), node.getNodeType());
		return true;
	}
	
	private static int getComplexity(String expression, int nodeType) {
		int cc = 1;
		char[] chars = expression.toCharArray();

		if (nodeType != ASTNode.CATCH_CLAUSE) {
			for (int i = 0; i < chars.length - 1; i++) {
				char next = chars[i];
				if ((next == '&' || next == '|') && (next == chars[i + 1])) {
					cc++;
				}
			}
		} else {
			for (int i = 0; i < chars.length - 1; i++) {
				if (chars[i] == '|') {
					cc++;
				}
			}
		}

		return cc;
	}
}
