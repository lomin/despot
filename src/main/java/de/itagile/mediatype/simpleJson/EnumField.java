package de.itagile.mediatype.simpleJson;

import de.itagile.model.Model;
import de.itagile.model.Key;
import org.json.simple.JSONObject;

import java.util.Set;

public class EnumField<T extends Enum> implements Key<T>, JsonFormat {
    public final String name;
    private final T undefined;
    private final Set<T> keys;

    public EnumField(String name, T undefined, Set<T> keys) {
        this.name = name;
        this.undefined = undefined;
        this.keys = keys;
    }

    @Override
    public T getUndefined() {
        return undefined;
    }

    @Override
    public void transform(Model e, JSONObject result) {
        T value = e.get(this);
        if (!keys.contains(value)) {
            value = getUndefined();
        }
        result.put(name, value.name());
    }
}
