package de.itagile.mediatype.simpleJson;

import de.itagile.model.Model;
import de.itagile.model.Key;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
