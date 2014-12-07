package de.itagile.mediatype.simpleJson;

import de.itagile.model.Key;
import de.itagile.model.Model;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequiredStringField<T> implements Key<T>, JsonFormat {
    public final String name;

    public RequiredStringField(String name) {
        this.name = name;
    }

    @Override
    public T getUndefined() {
        throw new IllegalStateException(this.name + " has been violated. It is required but has not been set.");
    }

    @Override
    public void transform(Model e, JSONObject result) {
        T value = e.get(this);
        result.put(name, value);
    }

    @Override
    public void spec(Map spec) {
        spec.put("name", name);
        Map type = new HashMap();
        type.put("name", "String");
        type.put("required", true);
        spec.put("type", type);
    }
}
