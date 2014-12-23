package de.itagile.despot;

public class StatelessResponsePartial<T> extends ResponsePartial<T> {

    @Override
    public ResponseModifier create(T t) {
        return this;
    }
}
