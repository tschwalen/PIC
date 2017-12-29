public class PrintCommand extends Node {
	public Parser parser;

	public PrintCommand() {}

	public PrintCommand(Parser parser){
		this.parser = parser;
	}

	public Object eval(){
		Object writee = parser.getVariable("writee");
		System.out.print(writee);
		return writee;
	}
}