public class PrintlnCommand extends Node {
	public Parser parser;

	public PrintlnCommand() {}

	public PrintlnCommand(Parser parser){
		this.parser = parser;
	}

	public Object eval(){
		Object writee = parser.getVariable("writee");
		System.out.println(writee);

		return writee;
	}
}