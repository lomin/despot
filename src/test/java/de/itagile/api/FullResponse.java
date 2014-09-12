package de.itagile.api;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponsePartial;
import de.itagile.mediatype.MediaTypeTest;
import de.itagile.model.HashModel;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;

public class FullResponse<T extends FullResponse.IFullResponseParams> extends ResponsePartial<T> {
    private IFullResponseParams param;

    private FullResponse() {
    }

    private FullResponse(IFullResponseParams param) {
        this.param = param;
    }

    public static FullResponse full_response() {
        return new FullResponse();
    }

    @Override
    public DespotResponse create(IFullResponseParams param) {
        return new FullResponse<>(param);
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder) {
        responseBuilder.header("tracking-header", "tracking-value");
    }

    @Override
    public Model responseModel() {
        Model e = new HashModel();
        e.update(MediaTypeTest.PRODUCT_ID_FIELD, param.getProductId());
        e.update(MediaTypeTest.TEMPLATE_NAME_FIELD, "/path/to/testTemplate");
        return e;
    }

    public static interface IFullResponseParams {
        String getProductId();
    }
}
