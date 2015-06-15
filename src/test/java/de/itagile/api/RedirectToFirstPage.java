package de.itagile.api;

import de.itagile.despot.ResponseModifier;
import de.itagile.despot.ResponseFactory;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.util.Map;

public class RedirectToFirstPage implements ResponseModifier {

    private RedirectToFirstPage() {
    }

    public static ResponseFactory<IProductSearchParams> redirect_to_first_page() {
        return new ResponseFactory<IProductSearchParams>() {
            @Override
            public ResponseModifier createResponseModifier(IProductSearchParams param) {
                return new RedirectToFirstPage();
            }
        };
    }

    @Override
    public void modify(Response.ResponseBuilder response, Model model) {
        response.header("redirect-header", "redirect-value");
    }

    @Override
    public void spec(Map spec) {

    }
}
