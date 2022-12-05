package assignment;

public class Token {

    // stores the type of the token. If it is a word/phrase, it also stores the word/phrase
    private String type, word;
    private boolean negation;

    public Token(String type) {
        this.type = type;
    }

    public Token(String type, String word, boolean negation) {
        this.type = type;
        this.word = word.toLowerCase();
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

    public String toString() {
        if (isWord()) {
            return type + " " + word + " " + negation;
        }
        else if (isPhrase()) {
            return type + " " + word;
        }
        else {
            return type;
        }
    }
}