package de.itagile.mediatype;

import de.itagile.ces.Entity;

public interface Format<T> {

    void put(Entity e, T result);
}
