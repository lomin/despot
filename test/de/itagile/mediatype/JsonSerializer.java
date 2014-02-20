package de.itagile.mediatype;

import de.itagile.ces.Entity;

import java.util.Map;
import java.util.Set;

public class JsonSerializer {

    public Map build(Entity e, Map result, Set<JsonFormat> mediaType) {
        for (Format<Map> key : mediaType) {
            key.put(e, result);
        }
        return result;
    }
}
