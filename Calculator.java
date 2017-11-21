import java.util.List;
import java.util.ArrayList;

// recursive descent parser calculator

class Calculator{
	
	public int currentTokenPosition = 0;
	public List<Token> tokens;

	public Token getToken(int offset){
		if(currentTokenPosition + offset >= tokens.size()){
			return new Token("", "NO_TOKEN");
		}
		return tokens.get(currentTokenPosition + offset);
	}

	public Token currentToken(){
		return getToken(0);
	}

	public void eatToken(int offset){
		currentTokenPosition = currentTokenPosition + offset;
	}

	public Token matchAndEat(String type){
		Token token = currentToken();

		if(!currentToken().type.equals(type)){
			System.out.println("Encountered" + token.type +
								" but " + type + " expected.");
			System.exit(0);
		}
		eatToken(1);
		return token;
	}



	// match a plus sign and return the next term
	public int add(){
		matchAndEat("ADD");
		return term();
	}

	public int subtract(){
		matchAndEat("SUBTRACT");
		return term();
	}


	// arithemtic expression : term ('+' | '-') term
	public int arithmeticExpression(){
		// left operand
		int result = term();

		while(currentToken().type.equals("ADD")||
			  currentToken().type.equals("SUBTRACT")){

			switch(currentToken().type){
				case "ADD":
					result = result + add();
					break;
				case "SUBTRACT":
					result = result - subtract();
					break;
			}
		}
		return result;
	}

	// factor : NUM | '(' arithmeticExpression ')'
	public int factor(){
		int result = 0;

		// if a lapren is encountered then we evaluate the expression contained within it first
		if(currentToken().type.equals("LEFT_PAREN")){
			matchAndEat("LEFT_PAREN");
			result = arithmeticExpression();
			matchAndEat("RIGHT_PAREN");
		}
		// base case, just return a number
		else if(currentToken().type.equals("NUMBER")){
			result = new Integer(currentToken().text).intValue();
			matchAndEat("NUMBER");
		}
		return result;
	}

	public int term(){
		int result = factor();
		while(currentToken().type.equals("MULTIPLY") ||
			  currentToken().type.equals("DIVIDE")){

			switch(currentToken().type){
				case "MULTIPLY":
					result *= multiply();
					break;
				case "DIVIDE":
					result /= divide();
					break;
			}
		}
		return result;
	}

	public int multiply(){
		matchAndEat("MULTIPLY");
		return factor();
	}

	public int divide(){
		matchAndEat("DIVIDE");
		return factor();
	}


	public static void main(String[] args){
		String expression = "((853+92*5)*10-20/2+771)";
		expression += " ";
		System.out.println("Expression: " + expression);
		// get the first character
		Calculator calc = new Calculator();
		Tokenizer tokenizer = new Tokenizer();
		// begin the recursive descent
		calc.tokens = tokenizer.tokenize(expression);
		System.out.println(calc.arithmeticExpression());
	}
}