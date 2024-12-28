package org.objexp.parsers.token;

public class DelimiterToken implements Comparable<DelimiterToken> {
    /**
     * The symbol which represents the delimiter.
     */
    private String symbol;
    /**
     * Determines if the delimiter requires whitespace before and after its position in the string.
     */
    private boolean whitespace;
    /**
     * Determines if the delimiter should be included in the parsed fragments array.
     */
    private boolean include;
    /**
     * Determines the precedence in which the delimiter is parsed relative to other delimiters.
     */
    private int precedence;

    public String getSymbol() {
        return symbol;
    }

    public DelimiterToken setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public boolean requiresWhitespace() {
        return whitespace;
    }

    public DelimiterToken setWhitespace(boolean whitespace) {
        this.whitespace = whitespace;
        return this;
    }

    public boolean shouldInclude() {
        return include;
    }

    public DelimiterToken setInclude(boolean include) {
        this.include = include;
        return this;
    }

    public int getPrecedence() {
        return precedence;
    }

    public DelimiterToken setPrecedence(int precedence) {
        this.precedence = precedence;
        return this;
    }

    @Override
    public int compareTo(DelimiterToken o) {
        if (o == null) {
            return 1;
        }
        return Integer.compare(this.getPrecedence(), o.getPrecedence());
    }
}
