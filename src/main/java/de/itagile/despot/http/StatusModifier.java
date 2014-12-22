package de.itagile.despot.http;

import de.itagile.despot.ResponseModifier;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.util.Map;

public class StatusModifier implements ResponseModifier {
    public static final String KEY = "status-code";
    private final Integer status;

    public StatusModifier(Integer status) {
        this.status = status;
    }

    public static ResponseModifier status(final Integer status) {
        return new StatusModifier(status);
    }

    @Override
    public void spec(Map spec) {
        spec.put(KEY, status.longValue());
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) {
        responseBuilder.status(status);
    }

    @Override
    public String toString() {
        return "Status: " + status;
    }
}
