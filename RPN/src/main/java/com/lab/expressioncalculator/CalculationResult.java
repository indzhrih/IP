package main.java.com.lab.expressioncalculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CalculationResult {

    private final BigDecimal numericResult;
    private final List<Token> reversePolishNotationTokens;

    public CalculationResult(BigDecimal numericResult, List<Token> reversePolishNotationTokens) {
        if (numericResult == null) {
            throw new IllegalArgumentException("Result an not be null.");
        }
        if (reversePolishNotationTokens == null || reversePolishNotationTokens.isEmpty()) {
            throw new IllegalArgumentException("RPN token list can not be null");
        }
        this.numericResult = numericResult;
        this.reversePolishNotationTokens = Collections.unmodifiableList(new ArrayList<>(reversePolishNotationTokens));
    }

    public BigDecimal getNumericResult() {
        return numericResult;
    }

    public List<Token> getReversePolishNotationTokens() {
        return reversePolishNotationTokens;
    }

    public String getNumericResultAsString() {
        BigDecimal normalized = numericResult.stripTrailingZeros();
        if (normalized.scale() < 0) normalized = normalized.setScale(0);
        return normalized.toPlainString();
    }

    public String getReversePolishNotationAsString() {
        return reversePolishNotationTokens.stream().map(Token::getText).collect(Collectors.joining(" "));
    }
}
