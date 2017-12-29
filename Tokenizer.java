import java.util.List;
import java.util.ArrayList;


class Tokenizer{

	public boolean isOp(char chr){
		return "+-*/<>=!|&".contains(chr + "");
	}

	public TokenType findStatementType(String str){
		TokenType type = TokenType.UNKNOWN;
		switch(str){
			case "script":
				type = TokenType.SCRIPT;
			break;
			case "end":
				type = TokenType.END;
				break;
			case "while":
				type = TokenType.WHILE;
				break;
			case "if":
				type = TokenType.IF;
				break;
			case "else":
				type = TokenType.ELSE;
				break;
			case "def":
				type = TokenType.DEF;
				break;
			default:
				type = TokenType.KEYWORD;
		}
		return type;
	}

	public TokenType findOpType(char firstOperator, char nextChar){
		TokenType type = TokenType.UNKNOWN;
		switch(firstOperator){
			case '+':
				type = TokenType.ADD;
			break;

			case '-':
				type = TokenType.SUBTRACT;
			break;

			case '*':
				type = TokenType.MULTIPLY;
			break;

			case '/':
				type = TokenType.DIVIDE;
			break;

			case '<':
				type = TokenType.LESS;
				if(nextChar == '=') type = TokenType.LESSEQUAL;
			break;

			case '>':
				type = TokenType.GREATER;
				if(nextChar == '=') type = TokenType.GREATEREQUAL;
			break;

			case '=':
				type = TokenType.ASSIGNMENT;
				if(nextChar == '=') type = TokenType.EQUAL;
			break;

			case '!':
				type = TokenType.NOT;
				if(nextChar == '=') type = TokenType.NOTEQUAL;
			break;

			case '|':
				type = TokenType.OR;
			break;

			case '&':
				type = TokenType.AND;
			break;
		}	
		return type;
	}

	public boolean isPunc(char chr){
		boolean puncOp = chr == ',';
		return puncOp;
	}

	public TokenType findPuncType(char firstOperator){
	TokenType type = TokenType.UNKNOWN;
	switch(firstOperator){
		case ',':
			type = TokenType.COMMA;
		break;
	}
	return type;
}

	public boolean isParen(char chr){
		return chr == '(' || chr == ')' ||
				chr == '[' || chr == ']' ||
				chr == ',';
	}

	public TokenType findParenType(char chr){
		if(chr == '(') return TokenType.LEFT_PAREN;
		if(chr == ')') return TokenType.RIGHT_PAREN;
		if(chr == '[') return TokenType.LEFT_BRACKET;
		if(chr == ']') return TokenType.RIGHT_BRACKET;
		if(chr == ',') return TokenType.COMMA;
		return TokenType.UNKNOWN; 
	}

	public List<Token> tokenize(String source){
		List<Token> tokens = new ArrayList<Token>();
		Token token = null;
		
		String tokenText = "";
		char firstOperator = '\0';
		TokenizeState state = TokenizeState.DEFAULT;

		for(int i = 0; i < source.length(); i++){
			char chr = source.charAt(i);
			switch(state){
				case DEFAULT:
					if(isOp(chr)){
						firstOperator = chr;
						TokenType opType = findOpType(firstOperator, '\0');
						token = new Token(Character.toString(chr), opType);
						state = TokenizeState.OPERATOR;
					}
					else if (isPunc(chr)){
						TokenType puncType = findPuncType(chr);
						tokens.add(new Token(Character.toString(chr), puncType));
					}
					else if(isParen(chr)){
						TokenType parenType = findParenType(chr);
						tokens.add(new Token(Character.toString(chr), parenType));
					}
					else if(Character.isLetter(chr)){
						tokenText += chr;
						state = TokenizeState.KEYWORD;
					}
					else if(Character.isDigit(chr)){
						tokenText += chr;
						state = TokenizeState.NUMBER;
					}
					else if(chr == '"'){
						state = TokenizeState.STRING;
					}
					else if(chr == '#'){
						state = TokenizeState.COMMENT;
					}
					break;
				case NUMBER:
					if(Character.isDigit(chr)){
						tokenText += chr;
					}
					else{
						tokens.add(new Token(tokenText, TokenType.NUMBER));
						tokenText = "";
						state = TokenizeState.DEFAULT;
						i--;
					}
					break;
				case KEYWORD:
					if(Character.isLetterOrDigit(chr)){
						tokenText += chr;
					}
					else{
						TokenType type = findStatementType(tokenText);
						tokens.add(new Token(tokenText, type));
						tokenText = "";
						state = TokenizeState.DEFAULT;
						i--;
					}
					break;
				case OPERATOR:
					if(isOp(chr)){
						TokenType opType = findOpType(firstOperator, chr);
						token = new Token(Character.toString(firstOperator) + Character.toString(chr), opType);
					}
					else{
						tokens.add(token);
						state = TokenizeState.DEFAULT;
						i--;
					}
					break;
				case STRING:
					if(chr == '"'){
						tokens.add(new Token(tokenText, TokenType.STRING));
						tokenText = "";
						state = TokenizeState.DEFAULT;
					}
					else {
						tokenText += chr;
					}
					break;
				case COMMENT:
					if(chr == '\n'){
						state = TokenizeState.DEFAULT;
					}
					break;
			}
		}
		return tokens;
	}
}