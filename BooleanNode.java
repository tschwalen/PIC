public class BooleanNode extends Node {
	Boolean value;

	public BooleanNode(){}

	public BooleanNode(Boolean value){
		this.value = value;
	}

	public Object eval(){
		return value;
	}

	public String toString(){
		return value + "";
	}
}