package de.itagile.mediatype.simpleJson;

import java.util.HashMap;
import java.util.Map;

public class IntegerField extends SimpleField<Integer> {
    public IntegerField(String name) {
        super(name);
    }

    @Override
    public void spec(Map spec) {
        spec.put("name", name);
        Map type = new HashMap();
        type.put("name", "Integer");
        spec.put("type", type);
    }
}
