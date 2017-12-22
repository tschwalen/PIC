import java.util.List;
import java.util.LinkedList;

public class WhileNode extends Node {

	public Node condition;
	public Node body;

	public WhileNode() {}

	public WhileNode(Node condition, Node body){
		this.condition = condition;
		this.body = body;
	}

	public Object eval(){
		Object ret = null;
		while( (Boolean)condition.eval()){
			ret = body.eval();
		}
		return ret;
	}
}