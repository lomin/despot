package de.itagile.mediatype;

import java.util.*;

public class MediaType<FormatType extends Format<?>> implements Iterable<FormatType> {

    private final Set<FormatType> mediaTypes = new HashSet<>();

    public Iterator<FormatType> iterator() {
        return mediaTypes.iterator();
    }

    public MediaType<FormatType> build(FormatType... types) {
        mediaTypes.addAll(Arrays.asList(types));
        return this;
    }
}
