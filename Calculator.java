// recursive descent parser calculator

class Calculator{
	// global variable for the intput string
	public static String expression = "";

	// current index in the input string
	public static int currentCharPostition = 0;

	// holds the next character
	public static char look;

	public static void getChar(){
		if(currentCharPostition < expression.length()){
			look = expression.charAt(currentCharPostition);
		}
		currentCharPostition++;
	}

	// parse an integer and return it
	public static int getNum(){
		int number = new Integer(look + "").intValue();
		getChar();
		return number;
	}

	public static void init(){
		getChar();
	}

	// consume the next character if it matches the input, otherwise throw an error
	public static void matchAndEat(char chr){
		if(look == chr){
			getChar();
		}
		else{
			System.out.println("Error: Unexpected Character");
			System.exit(0);
		}
	}

	// term : factor ('*' | '/')
	public static int term(){
		int result = factor();
		while((look == '*') || (look == '/') || (look == '%')){
			switch(look){
				case '*':
					result = result * multiply();
					break;
				case '/':
					result = result / divide();
					break;
				case '%':
					result = result % modulo();
			}
		}

		return result;
	}

	// match a plus sign and return the next term
	public static int add(){
		matchAndEat('+');
		return term();
	}

	public static int subtract(){
		matchAndEat('-');
		return term();
	}


	// arithemtic expression : term ('+' | '-') term
	public static int arithmeticExpression(){
		// left operand
		int result = term();

		while((look == '+') || (look == '-')){
			switch(look){
				case '+':
					result = result + add();
					break;
				case '-':
					result = result - subtract();
					break;
			}
		}

		return result;
	}

	// factor : NUM | '(' arithmeticExpression ')'
	public static int factor(){
		int result = 0;

		// if a lapren is encountered then we evaluate the expression contained within it first
		if(look == '('){
			matchAndEat('(');
			result = arithmeticExpression();
			matchAndEat(')');
		}
		// base case, just return a number
		else{
			result = getNum();
		}
		return result;
	}

	public static int multiply(){
		matchAndEat('*');
		return factor();
	}

	public static int divide(){
		matchAndEat('/');
		return factor();
	}

	public static int modulo(){
		matchAndEat('%');
		return factor();
	}

	public static void main(String[] args){
		expression = "((9*3-1+8)*5-7)%8";
		System.out.println("Expression: " + expression);
		// get the first character
		init();
		// begin the recursive descent
		System.out.println(arithmeticExpression());
	}
}