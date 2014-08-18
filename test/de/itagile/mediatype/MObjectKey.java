package de.itagile.mediatype;

import de.itagile.ces.Entity;
import de.itagile.ces.Key;
import org.json.simple.JSONObject;

public class MObjectKey implements Key<Entity>, JsonFormat {
    public final String name;
    private final Iterable<JsonFormat> keys;

    public MObjectKey(String name, Iterable<JsonFormat> keys) {
        this.name = name;
        this.keys = keys;
    }

    @Override
    public Entity getUndefined() {
        return null;
    }

    @Override
    public void serialize(Entity e, JSONObject result) {
        Entity subEntity = e.get(this);
        if (subEntity == null) return;
        JSONObject subType = new JSONObject();
        result.put(name, subType);
        for (JsonFormat key : keys) {
            key.serialize(subEntity, subType);
        }
    }
}
