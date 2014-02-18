package de.itagile.searchresponse;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;

import javax.ws.rs.core.Response;

public class RedirectToDeepestKnownItem extends ResponsePartial<ISearchParams> {

    @Override
    public DespotResponse create(ISearchParams param) {
        return new RedirectToDeepestKnownItem();
    }

    @Override
    public Response response() {
        return Response.ok().build();
    }
}
