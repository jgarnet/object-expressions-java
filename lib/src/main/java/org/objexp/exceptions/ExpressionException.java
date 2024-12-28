package org.objexp.exceptions;

public class ExpressionException extends Exception {
    private String expression;

    public ExpressionException(String message) {
        super(message);
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
