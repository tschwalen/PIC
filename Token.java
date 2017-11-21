class Token{
	public String text;
	public String type;

	public Token(String text, String type){
		this.text = text;
		this.type = type;
	}

	public String toString(){
		return "Text: " + text + " Type: " + type;
	}
}