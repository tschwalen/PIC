
// AST node for if-else construct. 
// Has a node for the conditional expression, the if body, and the else body.
// Upon evaluation, evaluate the condition, the evaluate the if body if the condition is true, 
// and the else body otherwise.
//
// EBNF:
// <if> ::= IF <bool-expression> <block> [ ELSE <block>] END

public class IfNode extends Node {

	public Node condition;
	public Node thenPart;
	public Node elsePart;

	public IfNode() {}

	public IfNode(Node condition, Node thenPart, Node elsePart){
		this.condition = condition;
		this.thenPart = thenPart;
		this.elsePart = elsePart;
	}

	public Object eval(){
		Object ret = null;

		if( (condition != null) && (thenPart != null)){
			if( (Boolean)condition.eval()){
				ret = thenPart.eval();
			}
		}
		if( (condition != null) && (elsePart != null)){
			if( !(Boolean)condition.eval()){
				ret = elsePart.eval();
			}
		}
		return ret;
	}
}