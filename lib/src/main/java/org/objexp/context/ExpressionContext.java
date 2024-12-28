package org.objexp.context;

import org.objexp.parsers.token.SymbolToken;
import org.objexp.parsers.token.TokenParser;

import java.util.Set;

public class ExpressionContext<T> {
    private String expression;
    private T object;
    private Set<SymbolToken> standardTokens;
    private TokenParser tokenParser;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public Set<SymbolToken> getStandardTokens() {
        return standardTokens;
    }

    public void setStandardTokens(Set<SymbolToken> standardTokens) {
        this.standardTokens = standardTokens;
    }

    public TokenParser getFragmentParser() {
        return tokenParser;
    }

    public void setFragmentParser(TokenParser tokenParser) {
        this.tokenParser = tokenParser;
    }
}
