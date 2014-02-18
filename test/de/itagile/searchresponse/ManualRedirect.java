package de.itagile.searchresponse;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;

import javax.ws.rs.core.Response;

public class ManualRedirect<T extends ManualRedirect.IManualRedirect> extends ResponsePartial<T> {
    private ManualRedirect() {
    }

    public static ManualRedirect manual_redirect() {
        return new ManualRedirect();
    }

    @Override
    public DespotResponse create(IManualRedirect param) {
        return manual_redirect();
    }

    @Override
    public Response response() {
        return Response.ok().build();
    }

    public static interface IManualRedirect {

    }
}
