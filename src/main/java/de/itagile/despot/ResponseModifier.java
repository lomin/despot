package de.itagile.despot;

import de.itagile.model.Model;

import javax.ws.rs.core.Response;

public interface ResponseModifier extends Specified {
    void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception;
}
