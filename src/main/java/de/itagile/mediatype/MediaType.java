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

    @Override
    public String toString() {
        return "MediaType{" +
                "name='" + name + '\'' +
                '}';
    }
}
