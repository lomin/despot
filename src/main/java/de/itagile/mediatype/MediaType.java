package de.itagile.mediatype;

import de.itagile.despot.DespotSpecParser;
import de.itagile.despot.EntityFactory;
import de.itagile.despot.ResponseModifier;
import de.itagile.despot.Specified;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.util.*;

import static java.util.Collections.emptyIterator;

public class MediaType<T, FormatType extends Format<T>> implements Iterable<FormatType>, EntityFactory<T>, ResponseModifier {

    public static final String KEY_PRODUCES = "produces";
    public static final String KEY_CONSUMES = "consumes";
    private final Set<FormatType> mediaTypes = new HashSet<>();
    private final String name;
    private final EntityFactory<T> entityFactory;

    public MediaType(String name, EntityFactory<T> entityFactory, FormatType... types) {
        this.name = name;
        this.entityFactory = entityFactory;
        this.mediaTypes.addAll(Arrays.asList(types));
    }

    @SuppressWarnings("unchecked")
    public static Map findMediaTypeByName(String name, Map mediaTypeSpec) {
        return findMediaTypeByName(name, (Set) mediaTypeSpec.get("mediatypes"));
    }

    public static Map findMediaTypeByName(String name, Set<Map> mediatypes) {
        if (mediatypes == null) {
            return Collections.emptyMap();
        }
        for (Map mediatype : mediatypes) {
            if (name.equals(mediatype.get("name"))) {
                return mediatype;
            }
        }
        return Collections.emptyMap();
    }

    public static DespotSpecParser.Transformation createConsumesTransformation() {
        return new DespotSpecParser.Transformation() {
            @Override
            public Map<String, Object> transform(Map<String, Object> spec) {
                if (!spec.containsKey(KEY_CONSUMES)) {
                    spec.put(KEY_CONSUMES, consumesNone().name());
                }
                return spec;
            }
        };
    }

    public static DespotSpecParser.NodeFactory createNode(final Map mediaTypeSpec, final String key) {
        return new DespotSpecParser.NodeFactory() {
            @Override
            public DespotSpecParser.Node create(Map<String, Object> result, Object value, DespotSpecParser.NodeFactoryMap extensionFields) {
                return new MediaTypeNode(result, value.toString(), mediaTypeSpec, key);
            }
        };
    }

    public static Consumes consumesNone() {
        return new Consumes() {
            @Override
            public String name() {
                return "NONE";
            }

            @Override
            public void spec(Map<String, Object> spec) {
                spec.put(KEY_CONSUMES, name());
            }
        };
    }

    public static Consumes consumes(final javax.ws.rs.core.MediaType mediaType) {
        return consumes(mediaType.toString());
    }

    public static Consumes consumes(final MediaType mediaType) {
        return new Consumes() {
            @Override
            public String name() {
                return mediaType.getName();
            }

            @Override
            public void spec(Map<String, Object> spec) {
                spec.put(KEY_CONSUMES, mediaType.getSpec());
            }
        };
    }

    public static Consumes consumes(final String mediaType) {
        return new Consumes() {
            @Override
            public String name() {
                return mediaType;
            }

            @Override
            public void spec(Map<String, Object> spec) {
                spec.put(KEY_CONSUMES, name());
            }
        };
    }

    public T modify(Model source) {
        T target = create();
        for (Format<T> key : this) {
            key.transform(source, target);
        }
        return target;
    }

    public Iterator<FormatType> iterator() {
        return new Iterator<FormatType>() {
            private final Iterator<FormatType> iterator = mediaTypes.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public FormatType next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public String getName() {
        return name;
    }

    public Map getSpec() {
        Map spec = new HashMap();
        spec.put("name", this.name);
        Set fields = new HashSet();
        for (FormatType format : mediaTypes) {
            Map subSpec = new HashMap();
            format.spec(subSpec);
            fields.add(subSpec);
        }
        spec.put("fields", fields);
        return spec;
    }

    @Override
    public String toString() {
        return "MediaType{" +
                "name='" + this.name + '\'' +
                '}';
    }

    @Override
    public T create() {
        return this.entityFactory.create();
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception {
        T entity = modify(model);
        responseBuilder.entity(entity).type(this.name);
    }

    @Override
    public void spec(Map<String, Object> spec) {
        spec.put(KEY_PRODUCES, getSpec());
    }

    public interface Consumes extends Specified {
        String name();
    }

    public static class MediaTypeNode implements DespotSpecParser.Node {
        private final Map<String, Object> parent;
        private final Map mediaTypeSpec;
        private final String key;
        private String value;

        public MediaTypeNode(Map<String, Object> parent, String value, Map mediaTypeSpec, String key) {
            this.parent = parent;
            this.value = value;
            this.mediaTypeSpec = mediaTypeSpec;
            this.key = key;
        }

        @Override
        public void call() {
            Set<Map> mediatypes = (Set) mediaTypeSpec.get("mediatypes");
            Map mediaType = findMediaTypeByName(value, mediatypes);
            if (Collections.emptyMap().equals(mediaType)) {
                parent.put(key, value);
            } else {
                parent.put(key, mediaType);
            }
        }

        @Override
        public Iterator<DespotSpecParser.Node> iterator() {
            return emptyIterator();
        }
    }
}
