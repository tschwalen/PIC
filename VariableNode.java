public class VariableNode extends Node {
	public String varName;
	public Parser parser;

	public VariableNode() {}

	public VariableNode(String varName, Parser parser){
		this.varName = varName;
		this.parser = parser;
	}

	public Object eval(){
		Object varValue = parser.getVariable(varName);
		if(varValue == null){
			System.out.println("Undefined Variable : " + varName);
			System.exit(1);
		}
		return varValue;
	}
}