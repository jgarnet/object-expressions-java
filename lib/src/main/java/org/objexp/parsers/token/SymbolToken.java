package org.objexp.parsers.token;

public class SymbolToken implements Comparable<SymbolToken> {
    /**
     * The symbol (or start symbol) that represents the token.
     */
    private String symbol;
    /**
     * The close symbol, if the token contains one.
     */
    private String closeSymbol;
    /**
     * Determines if the symbol / closeSymbol may be escaped using a backslash.
     */
    private boolean escapable;
    /**
     * Determines the precedence in which the token is parsed relative to other tokens.
     */
    private boolean _break;
    /**
     * Determines the precedence in which the token is parsed relative to other tokens.
     */
    private int precedence;

    public String getSymbol() {
        return symbol;
    }

    public SymbolToken setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public String getCloseSymbol() {
        return closeSymbol;
    }

    public SymbolToken setCloseSymbol(String closeSymbol) {
        this.closeSymbol = closeSymbol;
        return this;
    }

    public boolean isEscapable() {
        return escapable;
    }

    public SymbolToken setEscapable(boolean escapable) {
        this.escapable = escapable;
        return this;
    }

    public boolean shouldBreak() {
        return _break;
    }

    public SymbolToken setBreak(boolean _break) {
        this._break = _break;
        return this;
    }

    public int getPrecedence() {
        return precedence;
    }

    public SymbolToken setPrecedence(int precedence) {
        this.precedence = precedence;
        return this;
    }

    @Override
    public int compareTo(SymbolToken o) {
        if (o == null) {
            return 1;
        }
        // higher precedence should be prioritized
        int myPrecedence = this.getPrecedence();
        int theirPrecedence = o.getPrecedence();
        if (myPrecedence > theirPrecedence) {
            return -1;
        } else if (theirPrecedence > myPrecedence) {
            return 1;
        }
        return 0;
    }
}
