package de.itagile.searchresponse;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;

import javax.ws.rs.core.Response;

public class RedirectToFirstPageBracket extends ResponsePartial<ISearchParams> {
    private RedirectToFirstPageBracket() {
    }

    public static RedirectToFirstPageBracket redirect_to_first_page_bracket() {
        return new RedirectToFirstPageBracket();
    }

    @Override
    public DespotResponse create(ISearchParams param) {
        return redirect_to_first_page_bracket();
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder) {
    }
}
