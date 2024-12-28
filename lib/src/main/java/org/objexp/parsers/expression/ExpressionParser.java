package org.objexp.parsers.expression;

import org.objexp.context.ExpressionContext;
import org.objexp.exceptions.SyntaxException;

/**
 * Parses all tokens (groups, conditions, functions, etc.) within an expression.
 */
public interface ExpressionParser {
    /**
     * Parses an expression string to identify all groups, operators, conditions, functions, etc.
     * @param context The {@link ExpressionContext}.
     */
    <T> ExpressionNode parse(ExpressionContext<T> context) throws SyntaxException;
}