package de.itagile.api;

import de.itagile.despot.ResponseModifier;
import de.itagile.despot.ResponseFactory;
import de.itagile.mediatype.MediaTypeTest;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.util.Map;

public class WriteErrorMsg implements ResponseModifier {
    private WriteErrorMsg() {
    }

    public static ResponseFactory<IErrorMsg> write_error_msg() {
        return new ResponseFactory<IErrorMsg>() {
            @Override
            public ResponseModifier createResponseModifier(IErrorMsg param) {
                return new WriteErrorMsg();
            }
        };
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception {
        model.update(MediaTypeTest.REASON_FIELD, "Something went wrong.");
    }

    @Override
    public void spec(Map spec) {

    }

    public interface IErrorMsg {

    }
}
