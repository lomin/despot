package de.itagile.mediatype;

import de.itagile.despot.Specified;
import de.itagile.model.Model;

public interface Format<T> extends Specified {

    void transform(Model source, T target);

}
