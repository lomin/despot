package de.itagile.mediatype;

import de.itagile.ces.Entity;
import de.itagile.ces.Key;
import de.itagile.ces.UndefinedEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MHTMLModelKey implements Key<Entity>, HtmlFormat {

    private static Entity UNDEFINED = new UndefinedEntity();
    private final Set<JsonFormat> keys;

    public MHTMLModelKey(Set<JsonFormat> keys) {
        this.keys = keys;
    }

    @Override
    public void serialize(Entity e, Viewable result) {
        Entity subEntity = e.get(this);
        if (subEntity == UNDEFINED) return;
        Map model = new HashMap();
        result.setModel(model);
        for (JsonFormat key : keys) {
            key.serialize(subEntity, model);
        }
    }

    @Override
    public Entity getUndefined() {
        return UNDEFINED;
    }

}