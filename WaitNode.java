public class WaitNode extends Node {

	public Node interval;

	public WaitNode() {}

	public WaitNode(Node interval){
		this.interval = interval;
	}

	public Object eval(){
		int waitAmount = (Integer) interval.eval();

		try{
			Thread.sleep(waitAmount);
		}
		catch(Exception e){
			System.out.println("Error in WaitNode.eval() method " + e);
		}
		return waitAmount;
	}
}