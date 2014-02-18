package de.itagile.ces;

public interface Entity {
    <T> Entity attach(Key<T> k, T c);

    <T> T get(Key<T> key);
}
