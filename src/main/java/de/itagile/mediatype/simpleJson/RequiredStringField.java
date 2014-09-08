package de.itagile.mediatype.simpleJson;

import de.itagile.model.Model;
import de.itagile.model.Key;
import org.json.simple.JSONObject;

public class RequiredStringField<T> implements Key<T>, JsonFormat {
    public final String name;

    public RequiredStringField(String name) {
        this.name = name;
    }

    @Override
    public T getUndefined() {
        throw new IllegalStateException();
    }

    @Override
    public void transform(Model e, JSONObject result) {
        T value = e.get(this);
        result.put(name, value);
    }
}
