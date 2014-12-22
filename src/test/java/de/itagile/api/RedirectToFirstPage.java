package de.itagile.api;

import de.itagile.despot.ResponseModifier;
import de.itagile.despot.ResponsePartial;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;

public class RedirectToFirstPage extends ResponsePartial<IProductSearchParams> {

    private RedirectToFirstPage() {
    }

    public static RedirectToFirstPage redirect_to_first_page() {
        return new RedirectToFirstPage();
    }

    @Override
    public ResponseModifier create(IProductSearchParams param) {
        return redirect_to_first_page();
    }

    @Override
    public void modify(Response.ResponseBuilder response, Model model) {
        response.header("redirect-header", "redirect-value");
    }
}
