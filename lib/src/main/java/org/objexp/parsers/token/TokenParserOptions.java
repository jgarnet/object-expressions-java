package org.objexp.parsers.token;

public class TokenParserOptions {
    private boolean allowEmpty;

    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public TokenParserOptions setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
        return this;
    }
}
