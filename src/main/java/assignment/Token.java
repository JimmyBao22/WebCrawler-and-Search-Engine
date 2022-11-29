package assignment;

public class Token {

    private String type, word;
    private boolean negation;

    public Token(String type) {
        this.type = type;
    }

    public Token(String type, String word, boolean negation) {
        this.type = type;
        this.word = word;
        this.negation = negation;
    }

    public String getWord() {
        return word;
    }

    public boolean getNegation() {
        return negation;
    }

    public boolean isWord() {
        return type.equals("Word");
    }

    public boolean isPhrase() {
        return type.equals("Phrase");
    }

    public boolean isLeftParens() {
        return type.equals("Left Parenthesis");
    }

    public boolean isRightParens() {
        return type.equals("Right Parenthesis");
    }

    public boolean isAnd() {
        return type.equals("And");
    }

    public boolean isOr() {
        return type.equals("Or");
    }
}