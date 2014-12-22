package de.itagile.despot;

import javax.ws.rs.core.Response;

public interface ResponseModifier extends Specified {
    DespotResponse modify(Response.ResponseBuilder responseBuilder, DespotResponse despotResponse) throws Exception;
}
