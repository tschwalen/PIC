import java.util.List;
import java.util.ArrayList;

class Tokenizer{

	public String expression = "";
	public int currentCharPostition = 0;
	public char look;

	public void init(){
		getChar();
	}

	public void getChar(){
		if(currentCharPostition < expression.length()){
			look = expression.charAt(currentCharPostition);
		}
		currentCharPostition++;
	}

	public boolean isOp(char chr){
		return (chr == '+') || (chr == '-') ||
				(chr == '*') || (chr == '/') ||
				(chr == '(') || (chr == ')');
	}

	public String findOpType(char chr){
		String type = "UNKNOWN";
		switch(chr){
			case '+':
				type = "ADD";
				break;
			case '-':
				type = "SUBTRACT";
				break;
			case '*':
				type = "MULTIPLY";
				break;
			case '/':
				type = "DIVIDE";
				break;
			case '(':
				type = "LEFT_PAREN";
				break;
			case ')':
				type = "RIGHT_PAREN";
				break;
		}
		return type;
	}

	public List<Token> tokenize(String source){
		List<Token> tokens = new ArrayList<>();
		String token  = "";
		String state = "DEFAULT";

		for(int index = 0; index < source.length(); index++){
			char chr = source.charAt(index);
			switch(state){
				case "DEFAULT":
					String opType = findOpType(chr);
					if(isOp(chr)){
						tokens.add(new Token(chr + "", opType));
					}
					else if(Character.isDigit(chr)){
						token += chr;
						state = "NUMBER";
					}
					break;

				case "NUMBER":
					if(Character.isDigit(chr)){
						token += chr;
					}
					else{
						tokens.add(new Token(token, "NUMBER"));
						token = "";
						state = "DEFAULT";
						index--;
					}
					break;
			}
		}
		return tokens;
	}

	public void prettyPrint(List<Token> tokens){
		int numberCount = 0;
		int opCount = 0;

		for(Token token : tokens){
			if(token.type.equals("NUMBER")){
				System.out.println("Number....: " + token.text);
				numberCount++;
			}
			else {
				System.out.println("Operator..: " + token.type);
				opCount++;
			}
			System.out.println(numberCount + " numbers and " + opCount + " operators.");
		}
	}

	public static void main(String[] args){
		String expression = "219+341+19";
		expression += " ";
		// this is all bad practice, could all be static
		Tokenizer tokenizer = new Tokenizer();
		List<Token> tokens = tokenizer.tokenize(expression);
		System.out.println("--------------");
		tokenizer.prettyPrint(tokens);
	}
}