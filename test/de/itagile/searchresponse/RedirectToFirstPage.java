package de.itagile.searchresponse;

import de.itagile.model.HashModel;
import de.itagile.model.Model;
import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;
import de.itagile.mediatype.MediaTypeTest;

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
    public void modify(Response.ResponseBuilder response) {
        response.header("redirect-header", "redirect-value");
    }

    @Override
    public Model responseModel() {
        Model e = new HashModel();
        e.update(MediaTypeTest.PRODUCT_ID_FIELD, "testId123");
        e.update(MediaTypeTest.TEMPLATE_NAME_FIELD, "/path/to/testTemplate");
        return e;
    }
}
