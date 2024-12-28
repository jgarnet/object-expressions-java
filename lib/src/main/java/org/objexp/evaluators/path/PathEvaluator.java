package org.objexp.evaluators.path;

import org.objexp.exceptions.ExpressionException;

/**
 * Retrieves a value from an object given a path.
 */
public interface PathEvaluator<Type> {
    <Result> Result evaluate(Type object, String path) throws ExpressionException;
}