package main.java.com.lab.expressioncalculator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ExpressionCalculationService {

    private final ExpressionTokenizer expressionTokenizer;
    private final ReversePolishNotationConverter reversePolishNotationConverter;
    private final ReversePolishNotationEvaluator reversePolishNotationEvaluator;
    private final VariableAssignmentsParser variableAssignmentsParser;

    public ExpressionCalculationService(ExpressionTokenizer expressionTokenizer,
                                        ReversePolishNotationConverter reversePolishNotationConverter,
                                        ReversePolishNotationEvaluator reversePolishNotationEvaluator,
                                        VariableAssignmentsParser variableAssignmentsParser) {
        this.expressionTokenizer = expressionTokenizer;
        this.reversePolishNotationConverter = reversePolishNotationConverter;
        this.reversePolishNotationEvaluator = reversePolishNotationEvaluator;
        this.variableAssignmentsParser = variableAssignmentsParser;
    }

    public CalculationResult calculateExpression(String expression, String assignmentsText) {
        List<Token> tokens = expressionTokenizer.tokenize(expression);
        List<Token> reversePolishNotationTokens = reversePolishNotationConverter.convertToReversePolishNotation(tokens);
        Map<String, BigDecimal> variableValues = variableAssignmentsParser.parse(assignmentsText);
        BigDecimal numericResult = reversePolishNotationEvaluator.evaluate(reversePolishNotationTokens, variableValues);
        return new CalculationResult(numericResult, reversePolishNotationTokens);
    }
}
