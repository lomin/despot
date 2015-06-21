package de.itagile.mediatype.simpleJson;

import de.itagile.model.Key;
import de.itagile.model.Model;
import org.json.simple.JSONObject;

import java.util.*;

public class EnumSetField<T extends Enum> implements Key<Set<T>>, JsonFormat {

    public static final Set UNDEFINED = new HashSet<>();
    private final String name;
    private final T undefined;
    private final Collection<T> keys;

    public EnumSetField(String name, T undefined, T... keys) {
        this.name = name;
        this.undefined = undefined;
        this.keys = Arrays.asList(keys);
    }


    @Override
    public void transform(Model e, JSONObject result) {
        Set<T> entities = e.get(this);
        if (entities == UNDEFINED) return;
        Set set = new HashSet();
        for (T entity : entities) {
            if (!keys.contains(entity)) {
                entity = this.undefined;
            }
            set.add(entity.name());
        }
        result.put(name, set);
    }

    @Override
    public void spec(Map spec) {
        Map type = new HashMap();
        type.put("default", this.undefined.toString());
        type.put("name", "EnumSet");
        type.put("subtype", "String");
        Set<String> values = new HashSet<>();
        for (T key : keys) {
            values.add(key.name());
        }
        type.put("values", values);

        spec.put("name", name);
        spec.put("type", type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<T> getUndefined() {
        return UNDEFINED;
    }
}