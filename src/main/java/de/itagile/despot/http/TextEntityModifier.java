package de.itagile.despot.http;

import de.itagile.despot.ResponseModifier;
import de.itagile.despot.ResponseFactory;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.util.Map;

public class TextEntityModifier implements ResponseModifier {
    private final String text;

    private TextEntityModifier(String text) {
        this.text = text;
    }

    public static <T> ResponseFactory<T> text(final String text) {
        return new ResponseFactory<T>() {
            @Override
            public ResponseModifier createResponseModifier(T param) {
                return new TextEntityModifier(text);
            }
        };
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception {
        responseBuilder.entity(text);
    }

    @Override
    public void spec(Map<String, Object> spec) {

    }
}
