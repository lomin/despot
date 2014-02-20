package de.itagile.despot;

import de.itagile.ces.Entity;
import de.itagile.ces.HashEntity;

public abstract class ResponsePartial<ParamType> implements Completable<ParamType, DespotResponse>, DespotResponse {

    @Override
    public Entity response2() {
        return new HashEntity();
    }
}
