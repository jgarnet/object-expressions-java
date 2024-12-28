package org.objexp.parsers.token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objexp.exceptions.SyntaxException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseTokenParserTest {

    @Test
    public void testParsesCommaSeparatedFragments() throws SyntaxException {
        TokenParser parser = new BaseTokenParser();

        Set<SymbolToken> tokens = new HashSet<>();
        SymbolToken paren = new SymbolToken().setSymbol("(").setCloseSymbol(")").setEscapable(true);
        tokens.add(paren);
        SymbolToken brackets = new SymbolToken().setSymbol("[").setCloseSymbol("]").setEscapable(true);
        tokens.add(brackets);
        SymbolToken doubleQuote = new SymbolToken().setSymbol("\"").setEscapable(true);
        tokens.add(doubleQuote);
        SymbolToken slash = new SymbolToken().setSymbol("/").setEscapable(true);
        tokens.add(slash);

        Set<DelimiterToken> delimiters = new HashSet<>();
        DelimiterToken delimiter = new DelimiterToken();
        delimiter.setSymbol(",");
        delimiters.add(delimiter);

        List<String> expected = Arrays.asList(
                "TST(1,2,3)", "\"test, 1234\"", "12", "((test))", "$[some,field]", "/test=,,/", "(\\\\),)", "123"
        );
        List<String> actual = parser.parse("TST(1,2,3),\"test, 1234\",12,((test)),$[some,field],/test=,,/,(\\\\),),123", tokens, delimiters);
        Assertions.assertEquals(expected, actual);
    }

}

/*
it('should parse comma separated fragments with support for parentheses, strings, regex, and brackets', () => {
        expect(new BaseFragmentParser().parse(
            'TST(1,2,3),"test, 1234",12,((test)),$[some,field],/test=,,/,(\\),),123',
            new Set([
                { symbol: '(', closeSymbol: ')', escapable: true },
                { symbol: '[', closeSymbol: ']', escapable: true },
                { symbol: '"', escapable: true },
                { symbol: '/', escapable: true }
            ]),
            new Set([{ symbol: ',' }]))
        ).toEqual(['TST(1,2,3)', '"test, 1234"', '12', '((test))', '$[some,field]', '/test=,,/', '(\\),)', '123']);
    });
 */