package de.itagile.model;

public class UndefinedModel implements Model {
    @Override
    public <T> Model update(Key<T> k, T c) {
        return this;
    }

    @Override
    public <T> T get(Key<T> key) {
        return key.getUndefined();
    }
}
