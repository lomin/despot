package de.itagile.mediatype;

import de.itagile.ces.Entity;
import de.itagile.ces.Key;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Set;

public class MObjectKey implements Key<Entity>, JsonFormat {
    public final String name;
    private final Set<JsonFormat> keys;

    public MObjectKey(String name, Set<JsonFormat> keys) {
        this.name = name;
        this.keys = keys;
    }

    @Override
    public Entity getUndefined() {
        return null;
    }

    @Override
    public void put(Entity e, Map result) {
        Entity subEntity = e.get(this);
        if (subEntity == null) return;
        Map subType = new JSONObject();
        result.put(name, subType);
        for (JsonFormat key : keys) {
            key.put(subEntity, subType);
        }
    }
}
