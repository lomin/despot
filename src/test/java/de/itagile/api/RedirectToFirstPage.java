package de.itagile.api;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;

import javax.ws.rs.core.Response;

public class RedirectToFirstPage extends ResponsePartial<IProductSearchParams> {

    private RedirectToFirstPage() {
    }

    public static RedirectToFirstPage redirect_to_first_page() {
        return new RedirectToFirstPage();
    }

    @Override
    public DespotResponse create(IProductSearchParams param) {
        return redirect_to_first_page();
    }

    @Override
    public void modify(Response.ResponseBuilder response) {
        response.header("redirect-header", "redirect-value");
    }
}
