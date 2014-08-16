package de.itagile.mediatype;

import de.itagile.ces.Entity;
import de.itagile.ces.Key;

import java.util.Map;

public class MRequiredFieldKey<T> implements Key<T>, JsonFormat {
    public final String name;

    public MRequiredFieldKey(String name) {
        this.name = name;
    }

    @Override
    public T getUndefined() {
        throw new IllegalStateException();
    }

    @Override
    public void serialize(Entity e, Map result) {
        T value = e.get(this);
        result.put(name, value);
    }
}
