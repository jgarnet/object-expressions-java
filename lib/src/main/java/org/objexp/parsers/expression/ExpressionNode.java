package org.objexp.parsers.expression;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Node in an Expression.
 */
public class ExpressionNode {
    private String fragment;
    private boolean negate;
    private Link next;

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public Link getNext() {
        return next;
    }

    public void setNext(Link next) {
        this.next = next;
    }

    public static class Link {
        private ExpressionNode node;
        private Relationship relationship;

        public Link(ExpressionNode node, Relationship relationship) {
            this.node = node;
            this.relationship = relationship;
        }

        public ExpressionNode getNode() {
            return node;
        }

        public void setNode(ExpressionNode node) {
            this.node = node;
        }

        public Relationship getRelationship() {
            return relationship;
        }

        public void setRelationship(Relationship relationship) {
            this.relationship = relationship;
        }
    }

    public enum Relationship {
        AND("AND"), OR("OR");

        private final String value;
        private static final Map<String, Relationship> VALUES = new HashMap<>();
        static {
            for (Relationship value : values()) {
                VALUES.put(value.value, value);
            }
        }

        Relationship(String value) {
            this.value = value;
        }

        public static Relationship fromValue(String value) {
            return VALUES.get(value);
        }

    }
}