package org.objexp.evaluators.path;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.objexp.exceptions.ExpressionException;

import java.util.HashMap;
import java.util.Map;

/**
 * Internally transforms the Object into a Map, caches the result, and evaluates the path against the Map.
 * Lives for the duration of one Expression evaluation, and is re-used in recursive calls during evaluation.
 * This implementation is used by default in createContext() if no PathEvaluator is supplied via {@link org.objexp.context.ExpressionContext}.
 * @param <Type> Object type.
 */
public class CachedMapPathEvaluator<Type> implements PathEvaluator<Type> {
    /**
     * Caches the root Object Map (and any nested Objects in the recursive evaluation stack).
     */
    private final HashMap<Integer, Map<String, Object>> cache = new HashMap<>();
    /**
     * Used to transform an Object to a Map.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Determines whether Exceptions will be thrown during Path evaluation or ignored.
     * If ignored, evaluate() will return null when Exceptions are encountered.
     */
    private final boolean throwExceptions;

    public CachedMapPathEvaluator(boolean throwExceptions) {
        this.throwExceptions = throwExceptions;
    }

    public CachedMapPathEvaluator() {
        this(false);
    }

    @Override
    public <Result> Result evaluate(Type object, String path) throws ExpressionException {
        try {
            PathEvaluator<Map<String, Object>> evaluator = new MapPathEvaluator();
            int hashCode = object.hashCode();
            if (!this.cache.containsKey(hashCode)) {
                this.storeMap(object);
            }
            Map<String, Object> map = this.cache.get(hashCode);
            return evaluator.evaluate(map, path);
        } catch (Exception e) {
            if (this.throwExceptions) {
                throw e;
            }
            return null;
        }
    }

    private void storeMap(Type object) {
        Map<String, Object> result = this.objectMapper.convertValue(object, new TypeReference<Map<String, Object>>(){});
        this.cache.put(object.hashCode(), result);
    }
}
