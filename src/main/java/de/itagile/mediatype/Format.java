package de.itagile.mediatype;

import de.itagile.model.Model;

import java.util.Map;

public interface Format<T> {

    void transform(Model source, T target);
    void spec(Map spec);

}
