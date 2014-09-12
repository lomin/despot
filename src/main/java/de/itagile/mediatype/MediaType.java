package de.itagile.mediatype;

import java.util.*;

public class MediaType<FormatType extends Format<?>> implements Iterable<FormatType> {

    private final Set<FormatType> mediaTypes = new HashSet<>();
    private final String name;

    public MediaType(String name, FormatType... types) {
        this.name = name;
        mediaTypes.addAll(Arrays.asList(types));
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
        spec.put("fields", fields);
        for(FormatType format: mediaTypes) {
            Map subSpec = new HashMap();
            format.spec(subSpec);
            fields.add(subSpec);
        }
        return spec;
    }

    @Override
    public String toString() {
        return "MediaType{" +
                "name='" + name + '\'' +
                '}';
    }
}
