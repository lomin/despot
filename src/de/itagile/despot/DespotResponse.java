package de.itagile.despot;

import de.itagile.ces.Entity;

import javax.ws.rs.core.Response;

public interface DespotResponse {

    void modify(Response.ResponseBuilder responseBuilder);

    Entity response2();
}
