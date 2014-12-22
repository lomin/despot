package de.itagile.despot.http;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.ResponseModifier;

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
    public DespotResponse modify(Response.ResponseBuilder responseBuilder, DespotResponse despotResponse) {
        responseBuilder.status(status);
        return despotResponse;
    }

    @Override
    public String toString() {
        return "Status: " + status;
    }
}
