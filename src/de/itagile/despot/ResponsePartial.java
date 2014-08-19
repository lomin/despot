package de.itagile.despot;

import de.itagile.model.HashModel;
import de.itagile.model.Model;

public abstract class ResponsePartial<ParamType> implements Completable<ParamType, DespotResponse>, DespotResponse {

    @Override
    public Model responseModel() {
        return new HashModel();
    }
}
