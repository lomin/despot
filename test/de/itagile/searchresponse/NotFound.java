package de.itagile.searchresponse;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;

import javax.ws.rs.core.Response;

public class NotFound extends ResponsePartial<ISearchParams> {
    private NotFound() {
    }

    public static NotFound not_found() {
        return new NotFound();
    }

    @Override
    public DespotResponse create(ISearchParams param) {
        return not_found();
    }

    @Override
    public Response response() {
        return Response.ok().build();
    }
}
