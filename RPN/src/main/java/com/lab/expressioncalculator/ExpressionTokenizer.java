package main.java.com.lab.expressioncalculator;

import java.util.ArrayList;
import java.util.List;

public class ExpressionTokenizer {

    public List<Token> tokenize(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Выражение не задано.");
        }
        List<Token> tokens = new ArrayList<>();
        int length = expression.length();
        int index = 0;
        while (index < length) {
            char current = expression.charAt(index);
            if (Character.isWhitespace(current)) {
                index++;
                continue;
            }
            if (Character.isDigit(current) || current == '.' || current == ',') {
                StringBuilder numberBuilder = new StringBuilder();
                while (index < length) {
                    char ch = expression.charAt(index);
                    if (Character.isDigit(ch) || ch == '.' || ch == ',') {
                        numberBuilder.append(ch);
                        index++;
                    } else {
                        break;
                    }
                }
                String numberText = numberBuilder.toString().replace(',', '.');
                tokens.add(new Token(TokenType.NUMBER, numberText));
                continue;
            }
            if (Character.isLetter(current)) {
                StringBuilder identifierBuilder = new StringBuilder();
                while (index < length) {
                    char ch = expression.charAt(index);
                    if (Character.isLetterOrDigit(ch) || ch == '_') {
                        identifierBuilder.append(ch);
                        index++;
                    }
                    else break;
                }
                tokens.add(new Token(TokenType.VARIABLE, identifierBuilder.toString()));
                continue;
            }
            if (isOperatorCharacter(current)) {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(current)));
                index++;
                continue;
            }
            if (current == '(') {
                tokens.add(new Token(TokenType.LEFT_PARENTHESIS, String.valueOf(current)));
                index++;
                continue;
            }
            if (current == ')') {
                tokens.add(new Token(TokenType.RIGHT_PARENTHESIS, String.valueOf(current)));
                index++;
                continue;
            }
            throw new IllegalArgumentException("Unexpected symbol: " + current);
        }
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Expression may not be empty");
        }
        return tokens;
    }

    private boolean isOperatorCharacter(char character) {
        return character == '+' || character == '-' || character == '*' || character == '/' || character == '^';
    }
}
