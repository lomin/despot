package de.itagile.mediatype.simpleJson;

import java.util.HashMap;
import java.util.Map;

public class StringField extends SimpleField<String> {
    public StringField(String name) {
        super(name);
    }

    @Override
    public void spec(Map spec) {
        spec.put("name", name);
        Map type = new HashMap();
        type.put("name", "String");
        spec.put("type", type);
    }
}
