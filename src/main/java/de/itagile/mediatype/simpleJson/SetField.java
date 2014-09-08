package de.itagile.mediatype.simpleJson;

import de.itagile.model.Model;
import de.itagile.model.Key;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class SetField implements Key<Set<Model>>, JsonFormat {
    private final String name;
    private final Iterable<JsonFormat> keys;

    public SetField(String name, Iterable<JsonFormat> keys) {
        this.name = name;
        this.keys = keys;
    }

    @Override
    public Set<Model> getUndefined() {
        return null;
    }

    @Override
    public void transform(Model e, JSONObject result) {
        Set<Model> entities = e.get(this);
        if (entities == null) return;
        Set set = new HashSet<Object>();
        result.put(name, set);
        for (Model entity : entities) {
            JSONObject kv = new JSONObject();
            for (JsonFormat key : keys) {
                key.transform(entity, kv);
                set.add(kv);
            }
        }
    }
}
