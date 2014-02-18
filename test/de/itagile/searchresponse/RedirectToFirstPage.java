package de.itagile.searchresponse;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;

import javax.ws.rs.core.Response;

public class RedirectToFirstPage extends ResponsePartial<ISearchParams> {

    private RedirectToFirstPage() {
    }

    public static RedirectToFirstPage redirect_to_first_page() {
        return new RedirectToFirstPage();
    }

    @Override
    public DespotResponse create(ISearchParams param) {
        return redirect_to_first_page();
    }

    @Override
    public Response response() {
        return Response.status(301).build();
    }
}
