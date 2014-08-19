package de.itagile.mediatype.simpleJson;

import de.itagile.mediatype.simpleJson.JsonFormat;
import de.itagile.model.Model;
import de.itagile.model.Key;
import org.json.simple.JSONObject;

public class StringField<T> implements Key<T>, JsonFormat {
    public final String name;

    public StringField(String name) {
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
