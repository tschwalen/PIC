public class PrintNode extends Node {

	public Node expression;
	public String type;

	public PrintNode() {}

	public PrintNode(Node expression, String type){
		this.expression = expression;
		this.type = type;
	}

	public Object eval(){
		Object output = expression.eval();
		if(type.equals("sameline")) System.out.print(output);
		else if(type.equals("newline")) System.out.println(output);

		return output;
	}
}