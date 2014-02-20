package de.itagile.mediatype;

import de.itagile.ces.Entity;
import de.itagile.ces.Key;

import java.util.Map;

public class MFieldKey<T> implements Key<T>, JsonFormat {
    public final String name;

    public MFieldKey(String name) {
        this.name = name;
    }

    @Override
    public T getUndefined() {
        return null;
    }

    @Override
    public void put(Entity e, Map result) {
        T value = e.get(this);
        if (value != null) {
            result.put(name, value);
        }
    }
}
