package de.itagile.ces;

import java.util.HashMap;
import java.util.Map;

public class HashEntity implements Entity {

    private Map<Key<?>, Object> components = new HashMap<>();

    @Override
    public <T> Entity attach(Key<T> k, T c) {
        components.put(k, c);
        return this;
    }

    @Override
    public <T> T get(Key<T> key) {
        if (components.containsKey(key)) {
            return (T) components.get(key);
        } else {
            return key.getUndefined();
        }
    }

}
