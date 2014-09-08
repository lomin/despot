package de.itagile.mediatype;

import de.itagile.model.Model;

public interface Format<T> {

    void transform(Model source, T target);

}
