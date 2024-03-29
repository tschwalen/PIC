import java.util.List;
import java.util.LinkedList;
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
	public Node add(){
		matchAndEat(TokenType.ADD);
		return term();
	}

	public Node subtract(){
		matchAndEat(TokenType.SUBTRACT);
		return term();
	}


	public Node multiply(){
		matchAndEat(TokenType.MULTIPLY);
		return factor();
	}

	public Node divide(){
		matchAndEat(TokenType.DIVIDE);
		return factor();
	}


	// arithemtic expression : term ('+' | '-') term
	public Node arithmeticExpression(){
		// left operand
		Node node = term();

		while(isAddOp(currentToken().type)){

			switch(currentToken().type){
				case ADD:
					node = new BinOpNode(TokenType.ADD, node, add());
					break;
				case SUBTRACT:
					node = new BinOpNode(TokenType.SUBTRACT, node, subtract());
					break;
			}
		}
		return node;
	}

	public Node signedFactor(){
		if(currentToken().type == TokenType.SUBTRACT){
			matchAndEat(TokenType.SUBTRACT);
			Node node = new NegOpNode(factor());
			return node;
		}
		return factor();
	}

	// factor : NUM | '(' arithmeticExpression ')'
	public Node factor(){
		Node result = null;

		// if a lapren is encountered then we evaluate the expression contained within it first
		if(currentToken().type == TokenType.LEFT_PAREN){
			matchAndEat(TokenType.LEFT_PAREN);
			result = expression();
			matchAndEat(TokenType.RIGHT_PAREN);
		}
		// base case, just return a number
		else if(isNumber()){
			Token token = matchAndEat(TokenType.NUMBER);
			result = new NumberNode(new Integer(token.text));
		}
		return result;
	}

	public Node term(){
		Node node = signedFactor();
		while(isMulOp(currentToken().type)){

			switch(currentToken().type){
				case MULTIPLY:
					node = new BinOpNode(TokenType.MULTIPLY, node, multiply());
					break;
				case DIVIDE:
					node = new BinOpNode(TokenType.DIVIDE, node, divide());
					break;
			}
		}
		return node;
	}
	
	////////////////////////////////
	// Boolean methods begin here //
	////////////////////////////////

	public Node booleanExpression(){
		Node node = booleanTerm();
		while(isLogicalOp(currentToken().type)){
			if(currentToken().type == TokenType.OR){
				matchAndEat(TokenType.OR);
				node = new BinOpNode(TokenType.OR, node, booleanTerm());
			}
		}
		return node;
	}

	public Node booleanTerm(){
		Node node = notFactor();
		while(currentToken().type == TokenType.AND){
			matchAndEat(TokenType.AND);
			node = new BinOpNode(TokenType.AND, node, notFactor());
		}
		return node;
	}

	public Node notFactor(){
		if(currentToken().type == TokenType.NOT){
			matchAndEat(TokenType.NOT);
			Node node = booleanFactor();
			return new NotOpNode(node);
		}
		return booleanFactor();
	}

	public Node booleanFactor(){
		return relation();
	}

	public Node relation(){
		Node node = arithmeticExpression();
		
		if(isRelOp(currentToken().type)){
			switch(currentToken().type){
				case LESS: return less(node);
				case LESSEQUAL: return lessEqual(node);
				case EQUAL: return equal(node);
				case GREATER: return greater(node);
				case GREATEREQUAL: return greaterEqual(node);
			}
		}
		return node;
	}

	public Node less(Node node){
		matchAndEat(TokenType.LESS);
		return new BinOpNode(TokenType.LESS, node, arithmeticExpression());
	}

	public Node lessEqual(Node node){
		matchAndEat(TokenType.LESSEQUAL);
		return new BinOpNode(TokenType.LESSEQUAL, node, arithmeticExpression());
	}

	public Node equal(Node node){
		matchAndEat(TokenType.EQUAL);
		return new BinOpNode(TokenType.EQUAL, node, arithmeticExpression());
	}

	public Node greater(Node node){
		matchAndEat(TokenType.GREATER);
		return new BinOpNode(TokenType.GREATER, node, arithmeticExpression());
	}

	public Node greaterEqual(Node node){
		matchAndEat(TokenType.GREATEREQUAL);
		return new BinOpNode(TokenType.GREATEREQUAL, node, arithmeticExpression());
	}

	public Node notEqual(Node node){
		matchAndEat(TokenType.NOTEQUAL);
		return new BinOpNode(TokenType.NOTEQUAL, node , arithmeticExpression());
	}

	// recognizers
	public boolean isMulOp(TokenType type){
		return type == TokenType.MULTIPLY || type == TokenType.DIVIDE;
	}

	public boolean isAddOp(TokenType type){
		return type == TokenType.ADD || type == TokenType.SUBTRACT;
	}

	public boolean isMultiDigitOp(TokenType type){
		return type == TokenType.LESSEQUAL || type == TokenType.GREATEREQUAL;
	}

	public boolean isRelOp(TokenType type){
		return type == TokenType.LESS || type == TokenType.GREATER ||
				type == TokenType.EQUAL || type == TokenType.NOTEQUAL ||
				isMultiDigitOp(type);
	}

	public boolean isLogicalOp(TokenType type){
		return type == TokenType.OR || type == TokenType.AND;
	}

	public boolean isNumber(){
		return currentToken().type == TokenType.NUMBER;
	}


	////////////////////////



	public Node expression(){
		return booleanExpression();
	}


	public static void main(String[] args){
		
		Tokenizer tokenizer = new Tokenizer();
		Calculator calc = new Calculator();

		String conditionExpr = "1<10 ";
		String bodyExpr = "10+20 ";

		calc.tokens = tokenizer.tokenize(conditionExpr);
		Node node = calc.booleanExpression();
		boolean condition = (Boolean) node.eval();

		calc.currentTokenPosition = 0;
		calc.tokens = tokenizer.tokenize(bodyExpr);
		Node body = calc.booleanExpression();

		int count = 0;
		while( condition ){
			int result = (Integer) body.eval();
			System.out.println(result);
			if(count == 5) break;
			count++;
		}
	}
}