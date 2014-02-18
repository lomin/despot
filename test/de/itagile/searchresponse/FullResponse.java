package de.itagile.searchresponse;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;

import javax.ws.rs.core.Response;

public class FullResponse extends ResponsePartial<ISearchParams> {
    private FullResponse() {
    }

    public static FullResponse full_response() {
        return new FullResponse();
    }

    @Override
    public DespotResponse create(ISearchParams param) {
        return full_response();
    }

    @Override
    public Response response() {
        return Response.ok().build();
    }
}
