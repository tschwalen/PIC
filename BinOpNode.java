// AST node for all of the binary operations in the language
//
// Stores right and left operands as nodes -- these may be elemental values or more 
// expressions. Right and Left nodes must be evaluated before the operation can be
// applied.

// EBNF
// 


public class BinOpNode extends Node {
	public TokenType op;
	public Node left;
	public Node right;

	public BinOpNode(){}

	public BinOpNode(TokenType op, Node left, Node right){
		this.op = op;
		this.left = left;
		this.right = right;
	}

	public int toInt(Node node){
		Object res = node.eval();
		return (Integer) res;
	}

	public boolean toBoolean(Node node){
		Object res = node.eval();
		return (Boolean) res;
	}

	public Object toObject(Node node){
		return node.eval();		
	}

	public Object eval(){
		Object result = null;
		switch(op){
			case ADD:
				result = new Integer(toInt(left) + toInt(right));
				break;
			case SUBTRACT:
				result = new Integer(toInt(left) - toInt(right));
				break;
			case MULTIPLY:
				result = new Integer(toInt(left) * toInt(right));
				break;
			case DIVIDE:
				if(toInt(right) == 0){
					System.out.println("Error: Division by Zero.");
					System.exit(0);
				}
				result = new Integer(toInt(left) / toInt(right));
				break;
			case LESS:
				result = new Boolean(toInt(left) < toInt(right));
				break;
			case GREATER:
				result = new Boolean(toInt(left) > toInt(right));
				break;
			case EQUAL:
				result = new Boolean(toObject(left).equals(toObject(right)));
				break;
			case NOTEQUAL:
				result = new Boolean(!toObject(left).equals(toObject(right)));
				break;
			case LESSEQUAL:
				result = new Boolean(toInt(left) <= toInt(right));
				break;
			case GREATEREQUAL:
				result = new Boolean(toInt(left) >= toInt(right));
				break;
			case OR:
				result = new Boolean(toBoolean(left) || toBoolean(right));
				break;
			case AND:
				result = new Boolean(toBoolean(left) && toBoolean(right));
				break;
		}
		return result;
	}

	public static void main(String args[]){
		NumberNode firstNumber = new NumberNode(100);
		NumberNode secondNumber = new NumberNode(200);
		Node sumNode = new BinOpNode(TokenType.ADD, firstNumber, secondNumber);
		System.out.println("100 + 200 = " + sumNode.eval());

		Node compareNode = new BinOpNode(TokenType.LESS, firstNumber, secondNumber);
		System.out.println("100 < 200 = " + compareNode.eval());
	}
}