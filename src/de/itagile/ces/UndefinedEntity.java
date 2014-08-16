package de.itagile.ces;

public class UndefinedEntity implements Entity {
    @Override
    public <T> Entity attach(Key<T> k, T c) {
        return this;
    }

    @Override
    public <T> T get(Key<T> key) {
        return key.getUndefined();
    }
}
