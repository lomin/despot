package de.itagile.mediatype;

import de.itagile.ces.Entity;
import de.itagile.ces.Key;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MSetKey implements Key<Set<Entity>>, JsonFormat {
    private final String name;
    private final Set<JsonFormat> keys;

    public MSetKey(String name, Set<JsonFormat> keys) {
        this.name = name;
        this.keys = keys;
    }

    @Override
    public Set<Entity> getUndefined() {
        return null;
    }

    @Override
    public void put(Entity e, Map result) {
        Set<Entity> entities = e.get(this);
        if (entities == null) return;
        Set set = new HashSet<Object>();
        result.put(name, set);
        for (Entity entity : entities) {
            Map kv = new JSONObject();
            for (JsonFormat key : keys) {
                key.put(entity, kv);
                set.add(kv);
            }
        }
    }
}
