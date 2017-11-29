public class NumberNode extends Node {
	Integer value;

	public NumberNode(){}

	public NumberNode(Integer value){
		this.value = value;
	}

	public Object eval(){
		return value;
	}

	public String toString(){
		return value + "";
	}
}