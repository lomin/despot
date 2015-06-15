package de.itagile.api;

import de.itagile.despot.ResponseModifier;
import de.itagile.despot.ResponseFactory;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.util.Map;

public class ManualRedirect implements ResponseModifier {
    private ManualRedirect() {
    }

    public static ResponseFactory<IManualRedirect> manual_redirect() {
        return new ResponseFactory<IManualRedirect>() {
            @Override
            public ResponseModifier createResponseModifier(IManualRedirect param) {
                return new ManualRedirect();
            }
        };
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception {
    }

    @Override
    public void spec(Map spec) {

    }

    public static interface IManualRedirect {

    }
}
