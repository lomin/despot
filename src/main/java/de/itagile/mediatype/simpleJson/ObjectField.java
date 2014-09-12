package de.itagile.mediatype.simpleJson;

import de.itagile.model.Model;
import de.itagile.model.Key;
import org.json.simple.JSONObject;

import java.util.Map;

public class ObjectField implements Key<Model>, JsonFormat {
    public final String name;
    private final Iterable<JsonFormat> keys;

    public ObjectField(String name, Iterable<JsonFormat> keys) {
        this.name = name;
        this.keys = keys;
    }

    @Override
    public Model getUndefined() {
        return null;
    }

    @Override
    public void transform(Model model, JSONObject result) {
        Model childModel = model.get(this);
        if (childModel == null) return;
        JSONObject subType = new JSONObject();
        result.put(name, subType);
        for (JsonFormat key : keys) {
            key.transform(childModel, subType);
        }
    }

    @Override
    public void spec(Map spec) {

    }
}
