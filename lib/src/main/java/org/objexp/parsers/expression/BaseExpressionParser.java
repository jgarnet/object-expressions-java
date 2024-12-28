package org.objexp.parsers.expression;

import org.apache.commons.lang3.StringUtils;
import org.objexp.context.ExpressionContext;
import org.objexp.exceptions.SyntaxException;
import org.objexp.parsers.token.DelimiterToken;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseExpressionParser implements ExpressionParser {

    private static final Set<DelimiterToken> LOGICAL_OPERATORS = new HashSet<>();
    static {
        LOGICAL_OPERATORS.add(new DelimiterToken().setSymbol("AND").setWhitespace(true).setInclude(true));
        LOGICAL_OPERATORS.add(new DelimiterToken().setSymbol("OR").setWhitespace(true).setInclude(true));
        LOGICAL_OPERATORS.add(new DelimiterToken().setSymbol("NOT").setWhitespace(true).setInclude(true));
    }

    @Override
    public <T> ExpressionNode parse(ExpressionContext<T> context) throws SyntaxException {
        List<String> fragments = context.getFragmentParser().parse(
                context.getExpression(),
                context.getStandardTokens(),
                LOGICAL_OPERATORS
        );
        return this.buildNodes(fragments);
    }

    /**
     * Transforms all parsed fragments into an Expression Chain using {@link ExpressionNode}.
     * @param fragments All fragments parsed from the expression.
     */
    private <T> ExpressionNode buildNodes(List<String> fragments) throws SyntaxException {
        ExpressionNode root = new ExpressionNode();
        ExpressionNode current = root;
        for (int i = 0; i < fragments.size(); i++) {
            String fragment = fragments.get(i);
            if ("NOT".equals(fragment)) {
                if (StringUtils.isNotBlank(current.getFragment()) || i + 1 >= fragments.size()) {
                    throw new SyntaxException("Incomplete logical operation detected");
                }
                current.setNegate(!current.isNegate());
            } else if ("OR".equals(fragment) || "AND".equals(fragment)) {
                if (StringUtils.isNotBlank(current.getFragment()) || i + 1 >= fragments.size()) {
                    throw new SyntaxException("Incomplete logical operation detected");
                }
                ExpressionNode next = new ExpressionNode();
                current.setNext(new ExpressionNode.Link(next, ExpressionNode.Relationship.fromValue(fragment)));
                current = next;
            } else {
                current.setFragment(fragment);
            }
        }
        return root;
    }
}