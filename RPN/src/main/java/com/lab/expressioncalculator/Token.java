package main.java.com.lab.expressioncalculator;

public class Token {

    private final TokenType type;
    private final String text;

    public Token(TokenType type, String text) {
        if (type == null) {
            throw new IllegalArgumentException("Тип токена не может быть null.");
        }
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Текст токена не может быть пустым.");
        }
        this.type = type;
        this.text = text;
    }

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
