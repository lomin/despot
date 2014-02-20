package de.itagile.searchresponse;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;

import javax.ws.rs.core.Response;

public class BracketResponse extends ResponsePartial<ISearchParams> {
    private BracketResponse() {
    }

    public static BracketResponse bracket_response() {
        return new BracketResponse();
    }

    @Override
    public DespotResponse create(ISearchParams param) {
        return bracket_response();
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder) {
    }
}
