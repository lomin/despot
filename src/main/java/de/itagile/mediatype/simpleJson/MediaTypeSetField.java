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
    public static final Set<Model> UNDEFINED = new HashSet<>();
    private final String name;
    private final MediaType<JSONObject, JsonFormat> mediaType;

    public MediaTypeSetField(String name, MediaType<JSONObject, JsonFormat> mediaType) {
        this.name = name;
        this.mediaType = mediaType;
    }

    @Override
    public Set<Model> getUndefined() {
        return UNDEFINED;
    }

    @Override
    public void transform(Model e, JSONObject result) {
        Set<Model> entities = e.get(this);
        if (entities == UNDEFINED) return;
        Set set = new HashSet<>();
        for (Model entity : entities) {
            JSONObject kv = mediaType.modify(entity);
            set.add(kv);
        }
        result.put(name, set);
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
