package de.itagile.model;

public interface Model {
    <T> Model update(Key<T> k, T c);

    <T> T get(Key<T> key);
}
