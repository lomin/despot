package de.itagile.searchresponse;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;

import javax.ws.rs.core.Response;

public class RedirectToFirstPageFull extends ResponsePartial<ISearchParams> {
    private RedirectToFirstPageFull() {
    }

    public static RedirectToFirstPageFull redirect_to_first_page_full() {
        return new RedirectToFirstPageFull();
    }

    @Override
    public DespotResponse create(ISearchParams param) {
        return redirect_to_first_page_full();
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder) {
    }
}
