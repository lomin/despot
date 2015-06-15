package de.itagile.despot.http;

import de.itagile.despot.Specified;

import javax.ws.rs.core.MediaType;
import java.util.Map;

public class ConsumesSpecified {

    public static Specified consumes(final String mediaType) {
        return new Specified() {
            @Override
            public void spec(Map<String, Object> spec) {
                spec.put("consumes", mediaType);
            }
        };
    }
    public static Specified consumes(final MediaType mediaType) {
        return new Specified() {
            @Override
            public void spec(Map<String, Object> spec) {
                spec.put("consumes", mediaType.toString());
            }
        };
    }
}
