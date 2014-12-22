package de.itagile.mediatype.simpleJson;

import de.itagile.despot.EntityFactory;
import de.itagile.mediatype.MediaType;
import org.json.simple.JSONObject;

public class JsonMediaType extends MediaType<JSONObject, JsonFormat> {
    public JsonMediaType(String name, JsonFormat... jsonFormats) {
        super(name, new EntityFactory<JSONObject>() {
            @Override
            public JSONObject create() {
                return new JSONObject();
            }
        }, jsonFormats);
    }
}
