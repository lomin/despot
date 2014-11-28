package de.itagile.despot;

import de.itagile.model.Model;
import de.itagile.model.HashModel;

import javax.ws.rs.core.Response;

public abstract class ResponsePartial<ParamType> implements Completable<ParamType, DespotResponse>, DespotResponse {

    @Override
    public Model responseModel() {
        return new HashModel();
    }

    @Override
    public DespotResponse modify(Response.ResponseBuilder responseBuilder, DespotResponse despotResponse) {
        return despotResponse;
    }
}
