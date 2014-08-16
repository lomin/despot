package de.itagile.mediatype;

import de.itagile.ces.Entity;
import de.itagile.ces.Key;

import java.util.Map;
import java.util.Set;

public class MEnumKey<T extends Enum> implements Key<T>, JsonFormat {
    public final String name;
    private final T undefined;
    private final Set<T> keys;

    public MEnumKey(String name, T undefined, Set<T> keys) {
        this.name = name;
        this.undefined = undefined;
        this.keys = keys;
    }

    @Override
    public T getUndefined() {
        return undefined;
    }

    @Override
    public void serialize(Entity e, Map result) {
        T value = e.get(this);
        if (!keys.contains(value)) {
            value = getUndefined();
        }
        result.put(name, value.name());
    }
}
