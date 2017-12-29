import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class Util {

	public static void write(Object obj){
		System.out.print(obj);
	}

	public static void writeln(Object obj){
		System.out.println(obj);
	}

	public static void writeln(){
		System.out.println();
	}

	public static List<Node> createInlineFunctions(Parser parser){
		List<Node> inlineFunctions = new LinkedList<Node>();

		inlineFunctions.add(createPrintFunction(parser));
		inlineFunctions.add(createPrintlnFunction(parser));
		inlineFunctions.add(createWaitFunction(parser));
		inlineFunctions.add(createArraySizeFunction(parser));
		inlineFunctions.add(createStrLenFunction(parser));
		inlineFunctions.add(createStrConcatFunction(parser));

		return inlineFunctions;
	}

	public static Node createPrintFunction(Parser parser){
		String functionName = "print";
		List<Parameter> parameters = new ArrayList();
		parameters.add(new Parameter("writee"));

		List<Node> statements = new LinkedList<Node>();
		statements.add(new PrintCommand(parser));
		Node functionBody = new BlockNode(statements);

		Function function = new Function(functionName, functionBody, parameters);
		Node functionVariable = new AssignmentNode(functionName, function, parser);

		return functionVariable;
	}

	public static Node createPrintlnFunction(Parser parser){
		String functionName = "println";
		List<Parameter> parameters = new ArrayList();
		parameters.add(new Parameter("writee"));

		List<Node> statements = new LinkedList<Node>();
		statements.add(new PrintlnCommand(parser));
		Node functionBody = new BlockNode(statements);

		Function function = new Function(functionName, functionBody, parameters);
		Node functionVariable = new AssignmentNode(functionName, function, parser);

		return functionVariable;
	}

	public static Node createWaitFunction(Parser parser){
		String functionName = "wait";
		List<Parameter> parameters = new ArrayList();
		parameters.add(new Parameter("interval"));

		List<Node> statements = new LinkedList<Node>();
		statements.add(new WaitCommand(parser));
		Node functionBody = new BlockNode(statements);

		Function function = new Function(functionName, functionBody, parameters);
		Node functionVariable = new AssignmentNode(functionName, function, parser);

		return functionVariable;
	}

	public static Node createArraySizeFunction(Parser parser){
		String functionName = "arraySize";
		List<Parameter> parameters = new ArrayList();
		parameters.add(new Parameter("array"));

		List<Node> statements = new LinkedList<>();
		statements.add(new ArraySizeCommand(parser));
		Node functionBody = new BlockNode(statements);

		Function function = new Function(functionName, functionBody, parameters);
		Node functionVariable = new AssignmentNode(functionName, function, parser);

		return functionVariable;
	}

	public static Node createStrLenFunction(Parser parser){
		String functionName  = "strLen";
		List<Parameter> parameters = new ArrayList();
		parameters.add(new Parameter("str"));

		List<Node> statements = new LinkedList<Node>();
		statements.add(new StrLenCommand(parser));
		Node functionBody = new BlockNode(statements);

		Function function = new Function(functionName, functionBody, parameters);
		Node functionVariable = new AssignmentNode(functionName, function, parser);

		return functionVariable;
	}

	public static Node createStrConcatFunction(Parser parser){
		String functionName = "strConcat";
		List<Parameter> parameters = new ArrayList();
		parameters.add(new Parameter("str1"));
		parameters.add(new Parameter("str2"));

		List<Node> statements = new LinkedList<Node>();
		statements.add(new StrConcatCommand(parser));
		Node functionBody = new BlockNode(statements);

		Function function = new Function(functionName, functionBody, parameters);
		Node functionVariable = new AssignmentNode(functionName, function, parser);

		return functionVariable;
	}

}