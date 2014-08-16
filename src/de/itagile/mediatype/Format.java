package de.itagile.mediatype;

import de.itagile.ces.Entity;

public interface Format<T> {

    void serialize(Entity e, T result);

}
