package de.itagile.despot;

public interface Completable<S, T> {

    T create(S param);

}
