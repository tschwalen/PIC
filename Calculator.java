import java.util.List;
import java.util.ArrayList;

// recursive descent parser calculator

class Calculator{
	
	public int currentTokenPosition = 0;
	public List<Token> tokens;

	public Token getToken(int offset){
		if(currentTokenPosition + offset >= tokens.size()){
			return new Token("", TokenType.EOF);
		}
		return tokens.get(currentTokenPosition + offset);
	}

	public Token currentToken(){
		return getToken(0);
	}

	public Token nextToken(){
		return getToken(1);
	}

	public void eatToken(int offset){
		currentTokenPosition = currentTokenPosition + offset;
	}

	public Token matchAndEat(TokenType type){
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
		matchAndEat(TokenType.ADD);
		return term();
	}

	public int subtract(){
		matchAndEat(TokenType.SUBTRACT);
		return term();
	}


	public int multiply(){
		matchAndEat(TokenType.MULTIPLY);
		return factor();
	}

	public int divide(){
		matchAndEat(TokenType.DIVIDE);
		return factor();
	}


	// arithemtic expression : term ('+' | '-') term
	public int arithmeticExpression(){
		// left operand
		int result = term();

		while(currentToken().type == TokenType.ADD||
			  currentToken().type == TokenType.SUBTRACT){

			switch(currentToken().type){
				case ADD:
					result = result + add();
					break;
				case SUBTRACT:
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
		if(currentToken().type == TokenType.LEFT_PAREN){
			matchAndEat(TokenType.LEFT_PAREN);
			result = arithmeticExpression();
			matchAndEat(TokenType.RIGHT_PAREN);
		}
		// base case, just return a number
		else if(currentToken().type == TokenType.NUMBER){
			result = new Integer(currentToken().text).intValue();
			matchAndEat(TokenType.NUMBER);
		}
		return result;
	}

	public int term(){
		int result = factor();
		while(currentToken().type == TokenType.MULTIPLY ||
			  currentToken().type == TokenType.DIVIDE){

			switch(currentToken().type){
				case MULTIPLY:
					result *= multiply();
					break;
				case DIVIDE:
					result /= divide();
					break;
			}
		}
		return result;
	}
	
	// Boolean methods begin here

	public boolean booleanExpression(){
		boolean result = booleanTerm();
		while(currentToken().type == TokenType.OR){
			matchAndEat(TokenType.OR);
			result = result || booleanTerm();
		}
		return result;
	}

	public boolean booleanTerm(){
		boolean result = booleanFactor();
		while(currentToken().type == TokenType.AND){
			matchAndEat(TokenType.AND);
			result = result && booleanFactor();
		}
		return result;
	}

	public boolean booleanFactor(){
		return relation();
	}

	public boolean relation(){
		int leftExpr = arithmeticExpression();
		boolean result = false;
		TokenType type = currentToken().type;
		if( type == TokenType.EQUAL ||
			type == TokenType.LESS || 
			type == TokenType.GREATER ||
			type == TokenType.LESSEQUAL ||
			type == TokenType.GREATEREQUAL )
		{
			switch(type){
				case LESS: return less(leftExpr);
				case LESSEQUAL: return lessEqual(leftExpr);
				case EQUAL: return equal(leftExpr);
				case GREATER: return greater(leftExpr);
				case GREATEREQUAL: return greaterEqual(leftExpr);
			}
		}
		return result;
	}

	public boolean less(int leftExpr){
		matchAndEat(TokenType.LESS);
		return leftExpr < arithmeticExpression();
	}

	public boolean lessEqual(int leftExpr){
		matchAndEat(TokenType.LESSEQUAL);
		return leftExpr <= arithmeticExpression();
	}

	public boolean equal(int leftExpr){
		matchAndEat(TokenType.EQUAL);
		return leftExpr == arithmeticExpression();
	}

	public boolean greater(int leftExpr){
		matchAndEat(TokenType.GREATER);
		return leftExpr > arithmeticExpression();
	}

	public boolean greaterEqual(int leftExpr){
		matchAndEat(TokenType.GREATEREQUAL);
		return leftExpr >= arithmeticExpression();
	}

	////////////////////////

	public boolean expression(){
		return booleanExpression();
	}


	public static void main(String[] args){
		
		String expression = "5+7";
		expression += " ";
		Calculator calc = new Calculator();
		Tokenizer tokenizer = new Tokenizer();
		calc.tokens = tokenizer.tokenize(expression);


		System.out.println("Result: " + calc.expression());
	}
}