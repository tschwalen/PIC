public class NotOpNode extends Node {
	public Node node;

	public NotOpNode(){}

	public NotOpNode(Node node){
		this.node = node;
	}

	public boolean toBoolean(Node node){
		Object res = node.eval();
		return (Boolean) res;
	}

	public Object eval(){
		Object result = new Boolean(!toBoolean(node));
		return result;
	}
}