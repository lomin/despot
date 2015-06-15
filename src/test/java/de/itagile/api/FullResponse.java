package de.itagile.api;

import de.itagile.despot.ResponseModifier;
import de.itagile.despot.ResponseFactory;
import de.itagile.mediatype.MediaTypeTest;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.util.Map;

public class FullResponse implements ResponseModifier {
    private final IFullResponseParams param;

    private FullResponse(IFullResponseParams param) {
        this.param = param;
    }

    public static ResponseFactory<IFullResponseParams> full_response() {
        return new ResponseFactory<IFullResponseParams>() {
            @Override
            public ResponseModifier createResponseModifier(IFullResponseParams param) {
                return new FullResponse(param);
            }
        };
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) {
        responseBuilder.header("tracking-header", "tracking-value");
        model.update(MediaTypeTest.PRODUCT_ID_FIELD, param.getProductId());
        model.update(MediaTypeTest.TEMPLATE_NAME_FIELD, "/path/to/testTemplate");
    }

    @Override
    public void spec(Map<String, Object> spec) {

    }

    public interface IFullResponseParams {
        String getProductId();
    }
}
