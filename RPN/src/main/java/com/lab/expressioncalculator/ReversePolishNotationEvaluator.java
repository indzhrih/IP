package main.java.com.lab.expressioncalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class ReversePolishNotationEvaluator {

    public BigDecimal evaluate(List<Token> reversePolishNotationTokens, Map<String, BigDecimal> variableValues) {
        if (reversePolishNotationTokens == null || reversePolishNotationTokens.isEmpty()) {
            throw new IllegalArgumentException("RPN token list empty.");
        }
        Deque<BigDecimal> stack = new ArrayDeque<>();
        for (Token token : reversePolishNotationTokens) {
            if (token.getType() == TokenType.NUMBER) {
                BigDecimal value = parseNumber(token.getText());
                stack.push(value);
            }
            else if (token.getType() == TokenType.VARIABLE) {
                BigDecimal value = variableValues.get(token.getText());
                if (value == null) {
                    throw new IllegalArgumentException("Value for variable not found: " + token.getText());
                }
                stack.push(value);
            }
            else if (token.getType() == TokenType.OPERATOR) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Not enough operands for operator: " + token.getText());
                }
                BigDecimal right = stack.pop();
                BigDecimal left = stack.pop();
                BigDecimal result = applyOperator(token.getText(), left, right);
                stack.push(result);
            }
            else {
                throw new IllegalArgumentException("Incorrect token in RPN.");
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Incorrect expression, the balance in the stack is not equal to one element.");
        }
        return stack.pop();
    }

    private BigDecimal parseNumber(String text) {
        try {
            return new BigDecimal(text);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Incorrect number: " + text);
        }
    }

    private BigDecimal applyOperator(String operator, BigDecimal left, BigDecimal right) {
        if (operator.equals("+")) return left.add(right);
        if (operator.equals("-")) return left.subtract(right);
        if (operator.equals("*")) return left.multiply(right);
        if (operator.equals("/")) {
            if (right.compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException("Division by zero");
            }
            return left.divide(right, 10, RoundingMode.HALF_UP);
        }
        if (operator.equals("^")) {
            int exponent;
            try {
                exponent = right.intValueExact();
            }
            catch (ArithmeticException e) {
                throw new IllegalArgumentException("Degree must be an integer");
            }
            return left.pow(exponent);
        }
        throw new IllegalArgumentException("Unknown operator: " + operator);
    }
}
