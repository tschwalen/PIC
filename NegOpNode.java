public class NegOpNode extends Node {
	public Node node;

	public NegOpNode() {}

	public NegOpNode(Node node){
		this.node = node;
	}

	public int toInt(Node node){
		Object res = node.eval();
		return (Integer) res;
	}

	public Object eval(){
		Object result = new Integer(-toInt(node));
		return result;
	}
}