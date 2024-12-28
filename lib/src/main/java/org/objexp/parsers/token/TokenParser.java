package org.objexp.parsers.token;

import org.objexp.exceptions.SyntaxException;

import java.util.List;
import java.util.Set;

public interface TokenParser {
    List<String> parse(String str, Set<SymbolToken> tokens, Set<DelimiterToken> delimiters) throws SyntaxException;
}