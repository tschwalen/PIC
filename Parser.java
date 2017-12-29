import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

// recursive descent parser calculator

class Parser{
	
	public int currentTokenPosition = 0;
	public List<Token> tokens;

	public Map symbolTable = new HashMap();


	public Parser() {}

	public Parser(List<Token> tokens){
		this.tokens = tokens;
	}

	public List<Token> getTokens(){
		return tokens;
	}

	// Symbol table methods start //
	public Object setVariable(String name, Object value){
		symbolTable.put(name, value);
		return value;
	}

	public Object getVariable(String name){
		Object value = (Object) symbolTable.get(name);
		if(value != null) return value;

		return null;
	}

	// symbol table methods end //

	public RootNode program(){
		matchAndEat(TokenType.SCRIPT);
		return new RootNode(block(), Util.createInlineFunctions(this));
	}

	public BlockNode block(){
		List<Node> statements = new LinkedList<>();
		while( currentToken().type != TokenType.END){
			statements.add(statement());
		}
		matchAndEat(TokenType.END);
		return new BlockNode(statements);
	}


	// a statement is:
	// 		an assignment
	//		a while loop
	//		an if-else
	// 		a PRINT or PRINTLN
	// 		a Wait
	public Node statement(){
		Node node = null;
		TokenType type = currentToken().type;

		if(isAssignment()){
			node = assignment();
		}
		else if (isArrayAccess()){
			node = arrayUpdate();
		}
		else if(isFunctionCall()){
			node = functionCall();
		}
		else if(isWhile()){
			node = whileLoop();
		}
		else if(isIfElse()){
			node = ifStatement();
		}
		else if(isFunctionDef()){
			node = functionDefinition();
		}
		else {
			System.out.println("Unknown language construct: " 
				+ currentToken().text);
			System.exit(0);
		}
		return node;
	}


	///////////////////////////////////////////

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

	/////////////////////////////////////////

	public Node functionCall(){
		String functionName = matchAndEat(TokenType.KEYWORD).text;
		Node calleeFunctionName = new VariableNode(functionName, this);
		matchAndEat(TokenType.LEFT_PAREN);
		List<Parameter> actualParameters = functionCallParameters();
		matchAndEat(TokenType.RIGHT_PAREN);

		Node functionCallNode = new FunctionCallNode(calleeFunctionName, 
													actualParameters, this);
		return functionCallNode;
	}

	public Node functionDefinition(){
		matchAndEat(TokenType.DEF);
		String funcName = matchAndEat(TokenType.KEYWORD).text;

		matchAndEat(TokenType.LEFT_PAREN);
		List<Parameter> parameters = functionDefParameters();
		matchAndEat(TokenType.RIGHT_PAREN);

		Node functionBody = block();

		Function function = new Function(funcName, functionBody, parameters);
		Node functionVariable = new AssignmentNode(funcName, function, this);

		return functionVariable;
	}

	public Node arrayUpdate(){
		String arrayName = matchAndEat(TokenType.KEYWORD).text;
		Node array = new VariableNode(arrayName, this);

		matchAndEat(TokenType.LEFT_BRACKET);
		Node indexExpr = expression();
		matchAndEat(TokenType.RIGHT_BRACKET);

		matchAndEat(TokenType.ASSIGNMENT);
		Node rightSideExpr = expression();

		return new ArrayUpdateNode(array, indexExpr, rightSideExpr);
	}

	public Node arrayDefinition(String name){
		List<Node> elements = new ArrayList<>();
		matchAndEat(TokenType.LEFT_BRACKET);

		if(currentToken().type != TokenType.RIGHT_BRACKET){
			elements.add(expression());
			while(currentToken().type != TokenType.RIGHT_BRACKET){
				matchAndEat(TokenType.COMMA);
				elements.add(expression());
			}
		}
		matchAndEat(TokenType.RIGHT_BRACKET);
		return new AssignmentNode(name, new ArrayNode(elements), this);
	}

	public Node ifStatement(){
		Node condition = null, thenPart = null, elsePart = null;

		matchAndEat(TokenType.IF);
		condition = expression();
		thenPart = block();
		if(currentToken().type == TokenType.ELSE){
			matchAndEat(TokenType.ELSE);
			if(currentToken().type == TokenType.IF){
				elsePart = ifStatement();
			}
			else{
				elsePart = block();
			}
		}
		return new IfNode(condition, thenPart, elsePart);
	}

	public Node whileLoop(){
		Node condition, body;
		matchAndEat(TokenType.WHILE);
		condition = expression();
		body = block();
		return new WhileNode(condition, body);
	}

	public Node assignment(){
		Node node = null;
		// left arg is an identifier
		String name = matchAndEat(TokenType.KEYWORD).text;
		// eat the equals sign
		matchAndEat(TokenType.ASSIGNMENT);

		if(currentToken().type == TokenType.LEFT_BRACKET){
			node = arrayDefinition(name);
		}
		else{
			Node value = expression();
			node = new AssignmentNode(name, value, this);
		}

		return node;
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
		else if(isString()){
			Token token = matchAndEat(TokenType.STRING);
			result = new StringNode(new String(token.text));
		}
		else if (isKeyWord()){
			result = variable();
		}
		return result;
	}

	public Node variable(){
		Node node = null;
		if(nextToken().type == TokenType.LEFT_PAREN){
			node = functionCall();
		}
		else {
			Token token = matchAndEat(TokenType.KEYWORD);
			Node varNode = new VariableNode(token.text, this);

			if(currentToken().type == TokenType.LEFT_BRACKET){
				matchAndEat(TokenType.LEFT_BRACKET);
				Node key = expression();
				matchAndEat(TokenType.RIGHT_BRACKET);
				return new LookupNode((VariableNode) varNode, key);
			}
			else return varNode;
		}
		return node;
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

	// function definition parameters method

	public List functionDefParameters(){
		List<Parameter> parameters = null;
		if(currentToken().type == TokenType.KEYWORD){
			parameters = new ArrayList();
			parameters.add(new Parameter(matchAndEat(TokenType.KEYWORD).text));

			while(currentToken().type == TokenType.COMMA){
				matchAndEat(TokenType.COMMA);
				parameters.add(new Parameter(matchAndEat(TokenType.KEYWORD).text));
			}
		}
		return parameters;
	}

	/////////////////////////


	/////////////////////////////
	// recognizers
	/////////////////////////////


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

	public boolean isString(){
		return currentToken().type == TokenType.STRING;
	}

	public boolean isKeyWord(){
		return currentToken().type == TokenType.KEYWORD;
	}

	public boolean isAssignment(){
		TokenType type = currentToken().type;
		return type == TokenType.KEYWORD && nextToken().type == TokenType.ASSIGNMENT;
	}

	public boolean isWhile(){
		return currentToken().type == TokenType.WHILE;
	}

	public boolean isIfElse(){
		TokenType type = currentToken().type;
		return type == TokenType.IF || type == TokenType.ELSE;
	}

	public boolean isArrayAccess(){
		TokenType type = currentToken().type;
		return type == TokenType.KEYWORD && 
			nextToken().type == TokenType.LEFT_BRACKET;
	}

	public boolean isFunctionDef(){
		TokenType type = currentToken().type;
		return type == TokenType.DEF && nextToken().type == TokenType.KEYWORD;
	}

	public boolean isFunctionCall(){
		TokenType type = currentToken().type;
		return type == TokenType.KEYWORD && nextToken().type == TokenType.LEFT_PAREN;
	}


	////////////////////////
	// end of recognizers
	////////////////////////


	// "Because this behavior is so critically important... "
	public Object executeFunction(Function function, List boundParameters){
		Map<String, Object> savedSymbolTable = new HashMap<String, Object>(symbolTable);

		for(int index = 0; index < boundParameters.size(); index++){
			BoundParameter param = (BoundParameter) boundParameters.get(index);
			setVariable(param.getName(), param.getValue());
		}

		Object ret = function.eval();

		symbolTable = savedSymbolTable;

		return ret;
	}

	public List functionCallParameters(){
		List<Parameter> actualParameters = null;
		Node expression = expression();
		if(expression != null){
			actualParameters = new ArrayList();
			actualParameters.add(new Parameter(expression));
			while(currentToken().type == TokenType.COMMA){
				matchAndEat(TokenType.COMMA);
				actualParameters.add(new Parameter(expression()));
			}
		}

		return actualParameters;
	}



	public Node expression(){
		return booleanExpression();
	}


	public static void main(String[] args){
		
		Tokenizer tokenizer = new Tokenizer();
		Parser calc = new Parser();

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