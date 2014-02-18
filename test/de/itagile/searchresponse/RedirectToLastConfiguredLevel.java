package de.itagile.searchresponse;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;

import javax.ws.rs.core.Response;

public class RedirectToLastConfiguredLevel extends ResponsePartial<ISearchParams> {
    private RedirectToLastConfiguredLevel() {
    }

    public static RedirectToLastConfiguredLevel redirect_to_last_configured_level() {
        return new RedirectToLastConfiguredLevel();
    }

    @Override
    public DespotResponse create(ISearchParams param) {
        return redirect_to_last_configured_level();
    }

    @Override
    public Response response() {
        return Response.ok().build();
    }
}
