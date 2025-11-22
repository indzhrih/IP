package main.java.com.lab.expressioncalculator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ReversePolishNotationConverter {

    public List<Token> convertToReversePolishNotation(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Deque<Token> operatorStack = new ArrayDeque<>();

        for (Token token : tokens) {
            if (token.getType() == TokenType.NUMBER || token.getType() == TokenType.VARIABLE) {
                output.add(token);
            }
            else if (token.getType() == TokenType.OPERATOR) {
                while (!operatorStack.isEmpty() && operatorStack.peek().getType() == TokenType.OPERATOR) {
                    Token stackTop = operatorStack.peek();
                    if (hasHigherPrecedence(stackTop, token) || hasEqualPrecedence(stackTop, token) && isLeftAssociative(token)) {
                        output.add(operatorStack.pop());
                    }
                    else break;
                }
                operatorStack.push(token);
            }
            else if (token.getType() == TokenType.LEFT_PARENTHESIS) operatorStack.push(token);
            else if (token.getType() == TokenType.RIGHT_PARENTHESIS) {
                while (!operatorStack.isEmpty() && operatorStack.peek().getType() != TokenType.LEFT_PARENTHESIS) {
                    output.add(operatorStack.pop());
                }
                if (operatorStack.isEmpty() || operatorStack.peek().getType() != TokenType.LEFT_PARENTHESIS) {
                    throw new IllegalArgumentException("Brackets are not properly spaced");
                }
                operatorStack.pop();
            }
            else throw new IllegalArgumentException("Unexpected token");
        }

        while (!operatorStack.isEmpty()) {
            Token token = operatorStack.pop();
            if (token.getType() == TokenType.LEFT_PARENTHESIS || token.getType() == TokenType.RIGHT_PARENTHESIS) {
                throw new IllegalArgumentException("Brackets are not properly spaced");
            }
            output.add(token);
        }

        return output;
    }

    private boolean hasHigherPrecedence(Token first, Token second) {
        return getPrecedence(first.getText()) > getPrecedence(second.getText());
    }

    private boolean hasEqualPrecedence(Token first, Token second) {
        return getPrecedence(first.getText()) == getPrecedence(second.getText());
    }

    private int getPrecedence(String operator) {
        if (operator.equals("+") || operator.equals("-")) {
            return 1;
        }
        if (operator.equals("*") || operator.equals("/")) {
            return 2;
        }
        if (operator.equals("^")) {
            return 3;
        }
        throw new IllegalArgumentException("Unknown operator: " + operator);
    }

    private boolean isLeftAssociative(Token operatorToken) {
        String operator = operatorToken.getText();
        return !operator.equals("^");
    }
}
