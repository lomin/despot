package de.itagile.despot;

import de.itagile.model.HashModel;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.util.Map;

public abstract class ResponsePartial<ParamType> implements Completable<ParamType, DespotResponse>, DespotResponse {

    @Override
    public Model responseModel() {
        return new HashModel();
    }

    @Override
    public DespotResponse modify(Response.ResponseBuilder responseBuilder, DespotResponse despotResponse) {
        return despotResponse;
    }

    @Override
    public void spec(Map spec) {

    }
}
