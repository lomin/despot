package de.itagile.despot.http;

import de.itagile.despot.DespotSpecParser;
import de.itagile.despot.Specified;
import de.itagile.mediatype.MediaType.MediaTypeNode;

import javax.ws.rs.core.MediaType;
import java.util.Map;

public class ConsumesSpecified {

    public static final String KEY = "consumes";

    public static Consumes consumes(final String mediaType) {
        return new Consumes() {
            @Override
            public String name() {
                return mediaType;
            }

            @Override
            public void spec(Map<String, Object> spec) {
                spec.put(KEY, name());
            }
        };
    }

    public static Consumes consumes(final MediaType mediaType) {
        return consumes(mediaType.toString());
    }

    public static Consumes consumesNone() {
        return new Consumes() {
            @Override
            public String name() {
                return "NONE";
            }

            @Override
            public void spec(Map<String, Object> spec) {
                spec.put(KEY, name());
            }
        };
    }

    public static DespotSpecParser.NodeFactory createNode(final Map mediaTypeSpec) {
        return new DespotSpecParser.NodeFactory() {
            @Override
            public DespotSpecParser.Node create(Map<String, Object> result, Object value, DespotSpecParser.NodeFactoryMap extensionFields) {
                return new MediaTypeNode(result, value.toString(), mediaTypeSpec, KEY);
            }
        };
    }

    public static DespotSpecParser.Transformation createTransformation() {
        return new DespotSpecParser.Transformation() {
            @Override
            public Map<String, Object> transform(Map<String, Object> spec) {
                if (!spec.containsKey(KEY)) {
                    spec.put(KEY, consumesNone().name());
                }
                return spec;
            }
        };
    }

    public interface Consumes extends Specified {
        String name();
    }
}
