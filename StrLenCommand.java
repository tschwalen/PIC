public class StrLenCommand extends Node {

	public Parser parser;

	public StrLenCommand() {}

	public StrLenCommand(Parser parser){
		this.parser = parser;
	}

	public Object eval(){
		return new Integer(((String) parser.getVariable("str")).length());
	}
}