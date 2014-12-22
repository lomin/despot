package de.itagile.api;

import de.itagile.despot.ResponseModifier;
import de.itagile.despot.ResponsePartial;
import de.itagile.mediatype.MediaTypeTest;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;

public class FullResponse extends ResponsePartial<FullResponse.IFullResponseParams> {
    private final IFullResponseParams param;

    private FullResponse(IFullResponseParams param) {
        this.param = param;
    }

    public static FullResponse full_response() {
        return new FullResponse(null);
    }

    @Override
    public ResponseModifier create(IFullResponseParams param) {
        return new FullResponse(param);
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) {
        responseBuilder.header("tracking-header", "tracking-value");
        model.update(MediaTypeTest.PRODUCT_ID_FIELD, param.getProductId());
        model.update(MediaTypeTest.TEMPLATE_NAME_FIELD, "/path/to/testTemplate");
    }

    public static interface IFullResponseParams {
        String getProductId();
    }
}
