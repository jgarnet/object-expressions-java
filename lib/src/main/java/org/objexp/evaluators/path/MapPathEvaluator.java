package org.objexp.evaluators.path;

import org.apache.commons.lang3.StringUtils;
import org.objexp.exceptions.ExpressionException;
import org.objexp.parsers.token.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class MapPathEvaluator implements PathEvaluator<Map<String, Object>> {
    private static final Pattern NUMBER = Pattern.compile("^\\d+$");
    private static final Set<SymbolToken> SYMBOLS;
    private static final Set<DelimiterToken> DELIMITERS;
    static {
        SYMBOLS = new HashSet<>();
        SYMBOLS.add(new SymbolToken().setSymbol("[").setCloseSymbol("]").setEscapable(true));
        DELIMITERS = new HashSet<>();
        DELIMITERS.add(new DelimiterToken().setSymbol("."));
    }
    private final TokenParser tokenParser = new BaseTokenParser(new TokenParserOptions().setAllowEmpty(true));

    @Override
    @SuppressWarnings("unchecked")
    public <Result> Result evaluate(Map<String, Object> object, String path) throws ExpressionException {
        List<String> tokens = this.tokenParser.parse(path, SYMBOLS, DELIMITERS);
        Result result = (Result) object;
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (i == 0 && token.startsWith("$")) {
                token = token.substring(1);
            }
            if (StringUtils.isBlank(token)) {
                throw new ExpressionException(String.format("Received invalid path: %s", path));
            }
            if (NUMBER.matcher(token).matches()) {
                List<Object> list = (List<Object>) result;
                result = (Result) list.get(Integer.parseInt(token));
                continue;
            }
            if (token.startsWith("[") && token.endsWith("]")) {
                token = token.substring(1, token.length() - 1);
                token = StringUtils.replace(token, "\\[", "[");
                token = StringUtils.replace(token, "\\]", "]");
            }
            if (!((Map<String, Object>) result).containsKey(token)) {
                return null;
            }
            result = (Result) ((Map<String, Object>) result).get(token);
        }
        return result;
    }
}
