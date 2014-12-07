package de.itagile.despot;

import javax.ws.rs.core.Response;
import java.util.Map;

public class StatusModifier implements ResponseModifier {
    private final Integer status;

    public StatusModifier(Integer status) {
        this.status = status;
    }

    public static ResponseModifier status(final Integer status) {
        return new StatusModifier(status);
    }

    @Override
    public void spec(Map spec) {
        spec.put(DespotSpecParser.STATUS_CODE, status.longValue());
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
