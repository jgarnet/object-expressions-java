package org.objexp.parsers.token;

import org.apache.commons.lang3.StringUtils;
import org.objexp.exceptions.SyntaxException;

import java.util.*;
import java.util.regex.Pattern;

public class BaseTokenParser implements TokenParser {
    protected static final Pattern WHITESPACE = Pattern.compile("\\s");
    protected final TokenParserOptions options;

    public BaseTokenParser(TokenParserOptions options) {
        this.options = options;
    }

    public BaseTokenParser() {
        this(new TokenParserOptions());
    }

    @Override
    public List<String> parse(String string, Set<SymbolToken> tokens, Set<DelimiterToken> delimiters) throws SyntaxException {
        Map<String, List<SymbolToken>> tokensMap = this.mapTokens(tokens);
        Map<String, List<DelimiterToken>> delimitersMap = this.mapDelimiters(delimiters);
        int tokenCount = 0;
        String currentSymbol = "";
        List<String> fragments = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (int index = 0; index < string.length(); index++) {
            String c = String.valueOf(string.charAt(index));
            if (tokensMap.containsKey(c)) {
                List<SymbolToken> tokensList = tokensMap.get(c);
                SymbolToken token = null;
                boolean isStart = false;
                boolean isClose = false;
                for (SymbolToken _token : tokensList) {
                    if (this.isTokenSymbol(string, index, _token.getSymbol())) {
                        token = _token;
                        isStart = true;
                        break;
                    } else if (StringUtils.isNotBlank(_token.getCloseSymbol()) && this.isTokenSymbol(string, index, _token.getCloseSymbol())) {
                        token = _token;
                        isClose = true;
                        break;
                    }
                }
                if (!isStart && !isClose) {
                    buffer.append(c);
                    continue;
                }
                // this is a token symbol; append symbol to buffer
                int originalIndex = index;
                String symbol = isStart ? token.getSymbol() : token.getCloseSymbol();
                buffer.append(string, index, Math.max(index + symbol.length(), 1));
                // adjust index to account for remaining symbol characters
                index += symbol.length() - 1;
                if (token.isEscapable() && originalIndex - 1 >= 0 && string.charAt(originalIndex - 1) == '\\') {
                    // ignore escaped symbols only if currentSymbol matches
                    if (currentSymbol.equals(token.getSymbol()) || (StringUtils.isNotBlank(token.getCloseSymbol()) && currentSymbol.equals(token.getCloseSymbol()))) {
                        continue;
                    }
                }
                if (StringUtils.isBlank(currentSymbol)) {
                    // keep track of current token symbol
                    currentSymbol = symbol;
                } else if (!currentSymbol.equals(symbol)) {
                    // determine if current character is the start of the closeSymbol for the current token
                    SymbolToken _token = tokensMap.get(currentSymbol).get(0);
                    if (StringUtils.isBlank(_token.getCloseSymbol()) || !this.isTokenSymbol(string, originalIndex, _token.getCloseSymbol())) {
                        // this indicates we are inside another token / group; simply append to buffer and continue
                        continue;
                    }
                }
                if (StringUtils.isBlank(token.getCloseSymbol())) {
                    tokenCount = tokenCount == 1 ? 0 : 1;
                } else if (this.isTokenSymbol(string, originalIndex, token.getCloseSymbol())) {
                    tokenCount--;
                } else {
                    tokenCount++;
                }
                if (tokenCount < 0) {
                    throw this.createUnbalancedException(token);
                }
                if (tokenCount == 0) {
                    currentSymbol = "";
                    if (token.shouldBreak()) {
                        fragments.add(buffer.toString());
                        buffer.setLength(0);
                    }
                }
            } else {
                DelimiterToken delimiter = this.checkDelimiter(string, index, delimitersMap);
                if (delimiter != null && tokenCount == 0) {
                    // split values based on delimiter if we are not inside a token / group
                    this.addFragment(fragments, buffer.toString());
                    buffer.setLength(0);
                    if (delimiter.shouldInclude()) {
                        fragments.add(delimiter.getSymbol());
                        if (delimiter.getSymbol().length() > 1) {
                            index += delimiter.getSymbol().length() - 1;
                        }
                    }
                } else {
                    // neither a token group nor a delimiter; append character to buffer
                    buffer.append(c);
                }
            }
        }
        this.addFragment(fragments, buffer.toString());
        if (tokenCount != 0) {
            SymbolToken token = tokensMap.get(currentSymbol).get(0);
            throw this.createUnbalancedException(token);
        }
        return fragments;
    }

    private void addFragment(List<String> fragments, String fragment) {
        if (StringUtils.isNotBlank(fragment) || this.options.isAllowEmpty()) {
            fragments.add(StringUtils.defaultIfBlank(fragment, "").trim());
        }
    }

    private Map<String, List<SymbolToken>> mapTokens(Set<SymbolToken> tokens) {
        Map<String, List<SymbolToken>> tokensMap = new HashMap<>();
        for (SymbolToken token : tokens) {
            String symbol = token.getSymbol();
            String firstChar = this.getFirstSymbolChar(symbol);
            this.registerToken(tokensMap, firstChar, token);
            this.registerToken(tokensMap, symbol, token);
            String closeSymbol = token.getCloseSymbol();
            if (StringUtils.isNotBlank(closeSymbol)) {
                String closeFirstChar = this.getFirstSymbolChar(closeSymbol);
                this.registerToken(tokensMap, closeFirstChar, token);
                this.registerToken(tokensMap, closeSymbol, token);
            }
        }
        for (List<SymbolToken> values : tokensMap.values()) {
            this.sortPrecedence(values);
        }
        return tokensMap;
    }

    private void registerToken(Map<String, List<SymbolToken>> tokens, String symbol, SymbolToken token) {
        if (tokens.containsKey(symbol)) {
            tokens.get(symbol).add(token);
        } else {
            List<SymbolToken> list = new ArrayList<>();
            list.add(token);
            tokens.put(symbol, list);
        }
    }

    private Map<String, List<DelimiterToken>> mapDelimiters(Set<DelimiterToken> delimiters) {
        Map<String, List<DelimiterToken>> delimitersMap = new HashMap<>();
        for (DelimiterToken delimiter : delimiters) {
            String symbol = delimiter.getSymbol();
            String firstChar = this.getFirstSymbolChar(symbol);
            if (delimitersMap.containsKey(firstChar)) {
                delimitersMap.get(firstChar).add(delimiter);
            } else {
                List<DelimiterToken> list = new ArrayList<>();
                list.add(delimiter);
                delimitersMap.put(firstChar, list);
            }
        }
        for (List<DelimiterToken> values : delimitersMap.values()) {
            this.sortPrecedence(values);
        }
        return delimitersMap;
    }

    private <T extends Comparable<T>> void sortPrecedence(List<T> values) {
        values.sort(Comparable::compareTo);
    }

    private String getFirstSymbolChar(String symbol) {
        return String.valueOf(symbol.toLowerCase(Locale.ROOT).charAt(0));
    }

    private DelimiterToken checkDelimiter(String string, int index, Map<String, List<DelimiterToken>> delimitersMap) {
        String c = String.valueOf(string.toUpperCase(Locale.ROOT).charAt(index));
        if (delimitersMap.containsKey(c)) {
            List<DelimiterToken> delimiters = delimitersMap.get(c);
            for (DelimiterToken delimiter : delimiters) {
                String symbol = delimiter.getSymbol();
                int symbolLength = symbol.length();
                if (delimiter.requiresWhitespace()) {
                    if (index - 1 >= 0 && !WHITESPACE.matcher(String.valueOf(string.charAt(index - 1))).matches()) {
                        continue;
                    }
                    if (symbolLength == 1) {
                        return delimiter;
                    }
                    if (index + symbolLength - 1 < string.length()) {
                        if (symbol.equals(string.substring(index, index + symbolLength).toUpperCase(Locale.ROOT))) {
                            if (index + symbolLength - 1 == string.length() - 1 || WHITESPACE.matcher(String.valueOf(string.charAt(index + symbolLength))).matches()) {
                                return delimiter;
                            }
                        }
                    }
                }
                if (delimiter.getSymbol().length() == 1) {
                    return delimiter;
                }
            }
        }
        return null;
    }

    private boolean isTokenSymbol(String string, int index, String symbol) {
        int symbolLength = symbol.length();
        if (index + symbolLength - 1 > string.length() - 1) {
            return false;
        }
        return symbol.equals(string.substring(index, index + symbolLength));
    }

    private SyntaxException createUnbalancedException(SymbolToken token) {
        if (StringUtils.isNotBlank(token.getCloseSymbol())) {
            return new SyntaxException(String.format(
                    "Expression contains imbalanced symbol group: %s%s",
                    token.getSymbol(),
                    token.getCloseSymbol()
            ));
        }
        return new SyntaxException(String.format(
                "Expression contains imbalanced symbol: %s",
                token.getSymbol()
        ));
    }
}