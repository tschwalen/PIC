public class WaitCommand extends Node {
	public Parser parser;

	public WaitCommand() {}

	public WaitCommand(Parser parser){
		this.parser = parser;
	}
	
	public Object eval(){
		Integer waitAmount = (Integer) parser.getVariable("interval");
		try{
			Thread.sleep(waitAmount.intValue());
		} catch (Exception e){
			System.out.println("Error in WaitNode.eval() method" + e);
		}
		return waitAmount;
	}
}