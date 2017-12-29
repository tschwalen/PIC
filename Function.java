import java.util.List;

public class Function extends Node {
	private Node body;
	private List<Parameter> parameters;
	private String name;

	public Function(String name, Node body, List<Parameter> parameters){
		this.body = body;
		this.parameters = parameters;
		this.name = name;
	}

	public Object eval(){
		return body.eval();
	}

	public List<Parameter> getParameters(){
		return parameters;
	}

	public Node getBody(){
		return body;
	}

	public String getName(){
		return name;
	}
}