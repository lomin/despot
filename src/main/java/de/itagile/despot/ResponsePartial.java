package de.itagile.despot;

import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.util.Map;

public abstract class ResponsePartial<ParamType> implements Completable<ParamType, ResponseModifier>, ResponseModifier {

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception {
    }

    @Override
    public void spec(Map spec) {

    }
}
