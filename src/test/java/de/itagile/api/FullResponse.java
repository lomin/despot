package de.itagile.api;

import de.itagile.mediatype.MediaTypeTest;
import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;
import de.itagile.model.HashModel;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;

public class FullResponse extends ResponsePartial<IProductSearchParams> {
    private FullResponse() {
    }

    public static FullResponse full_response() {
        return new FullResponse();
    }

    @Override
    public DespotResponse create(IProductSearchParams param) {
        return full_response();
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder) {
        responseBuilder.header("tracking-header", "tracking-value");
    }

    @Override
    public Model responseModel() {
        Model e = new HashModel();
        e.update(MediaTypeTest.PRODUCT_ID_FIELD, "testId123");
        e.update(MediaTypeTest.TEMPLATE_NAME_FIELD, "/path/to/testTemplate");
        return e;
    }
}
