package de.itagile.mediatype.simpleJson;

import de.itagile.model.Key;
import de.itagile.model.Model;
import org.json.simple.JSONObject;

public abstract class SimpleField<T> implements Key<T>, JsonFormat {
    public final String name;

    public SimpleField(String name) {
        this.name = name;
    }

    @Override
    public T getUndefined() {
        return null;
    }

    @Override
    public void transform(Model e, JSONObject result) {
        T value = e.get(this);
        if (value != null) {
            result.put(name, value);
        }
    }
}
