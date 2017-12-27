import java.util.List;

public class LookupNode extends Node {
	private VariableNode varNode;
	private Node keyNode;

	public LookupNode(VariableNode varNode, Node keyNode){
		this.varNode = varNode;
		this.keyNode = keyNode;
	}

	public Object eval(){
		Object var = varNode.eval();
		int index = (Integer) keyNode.eval();
		Object ret = ((List) var).get(index);
		return ret;
	}
}