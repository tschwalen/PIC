import java.util.List;
import java.util.LinkedList;


// AST node for a block of code. 
// Contains a list of statements.
// Upon evalutation, iterate over the list of statements and evaluate them
//
// EBNF:
// <block> ::= (<statement>)+

public class BlockNode extends Node {

	private List<Node> statements;

	public BlockNode(List<Node> statements){
		this.statements = statements;
	}

	public Object eval(){
		Object ret = null;

		for(Node statement : statements){
			ret = statement.eval();
		}
		return ret;
	}

	public Node get(int index){
		return statements.get(index);
	}

	protected List<Node> getStatements(){
		return statements;
	}

	public String toString(){
		String str = "";
		for(Node statement : statements){
			str = str + statement + "\n";
		}
		return str;
	}
}