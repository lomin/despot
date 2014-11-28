package de.itagile.despot;

import javax.ws.rs.core.Response;

public interface ResponseModifier {
    DespotResponse modify(Response.ResponseBuilder responseBuilder, DespotResponse despotResponse);
}
