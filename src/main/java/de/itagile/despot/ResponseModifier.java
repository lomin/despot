package de.itagile.despot;

import javax.ws.rs.core.Response;

public interface ResponseModifier {
    void modify(Response.ResponseBuilder responseBuilder);
}
