package de.itagile.mediatype;

import de.itagile.despot.EntityFactory;
import org.json.simple.JSONObject;

public class JSONObjectEntityFactory implements EntityFactory<JSONObject> {
    @Override
    public JSONObject create() {
        return new JSONObject();
    }
}
