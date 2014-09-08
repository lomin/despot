package de.itagile.despot;

import de.itagile.model.Model;

import javax.ws.rs.core.Response;

public interface DespotResponse {

    void modify(Response.ResponseBuilder responseBuilder);

    Model responseModel();
}
