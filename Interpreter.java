import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.LinkedList;

public class Interpreter {

	public static void main(String args[]) {
		boolean debug = false;

		if(args.length < 1) {System.out.println("Usage: Demo <script>"); return;}
		if(args.length > 1) {
			if(args[1].equals("debug")){
				debug = true;
			}
		}

		Interpreter interpreter = new Interpreter();
		String sourceCode = interpreter.readFile(args[0]);
		interpreter.interpret(sourceCode, debug);
	}

	//
	public void interpret(String source, boolean debug){
		Tokenizer tokenizer = new Tokenizer();
		Parser parser = new Parser(tokenizer.tokenize(source));

		if(debug) dumpTokens(parser);

		parser.matchAndEat(TokenType.SCRIPT);
		Node script = parser.block();

		script.eval();
	}

	public void dumpTokens(Parser parser){
		for(Token token : parser.getTokens()){
			System.out.println("Type: " + token.type + " Text: " + token.text+" ");
		}
		System.out.println();
	}

	private String readFile(String path){
		FileInputStream stream = null;
		InputStreamReader input = null;

		try{
			stream = new FileInputStream(path);
			input = new InputStreamReader(stream, Charset.defaultCharset());

			Reader reader = new BufferedReader(input);
			StringBuilder builder = new StringBuilder();
			char[] buffer = new char[8192];
			int read;

			while((read = reader.read(buffer, 0, buffer.length)) > 0){
				builder.append(buffer, 0, read);
			}
			builder.append(" ");
			return builder.toString();
		}
		catch(FileNotFoundException e){
			System.out.println("FILE NOT FOUND. Error in Interpreter.java-> ReadFile(String path) method");
			System.exit(0);
		}
		catch(IOException e){
			System.out.println("Error while reading the script. Error in Interpreter.java-> ReadFile(String path) method");
			System.exit(0);
		}
		finally{
			try{
				input.close();
				stream.close();
			}
			catch (Exception e) {
				System.out.println("Error while closing a stream or a stream reader. ");
				System.exit(0);
			}
		}
		return null;
	}
}