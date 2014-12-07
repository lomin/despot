package de.itagile.mediatype.simpleJson;

import de.itagile.mediatype.MediaType;
import de.itagile.model.Key;
import de.itagile.model.Model;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MediaTypeSetField implements Key<Set<Model>>, JsonFormat {
    private final String name;
    private final MediaType<JSONObject, JsonFormat> mediaType;

    public MediaTypeSetField(String name, MediaType<JSONObject, JsonFormat> mediaType) {
        this.name = name;
        this.mediaType = mediaType;
    }

    @Override
    public Set<Model> getUndefined() {
        return null;
    }

    @Override
    public void transform(Model e, JSONObject result) {
        Set<Model> entities = e.get(this);
        if (entities == null) return;
        Set set = new HashSet<>();
        result.put(name, set);
        for (Model entity : entities) {
            JSONObject kv = mediaType.modify(entity);
            set.add(kv);
        }
    }

    @Override
    public void spec(Map spec) {
        spec.put("name", name);
        Map type = new HashMap();
        type.put("name", "MediaTypeSet");
        type.put("mediatype", mediaType.getName());
        spec.put("type", type);
    }
}
