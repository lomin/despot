package de.itagile.despot;

import de.itagile.specification.Specification;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public final class ResponseOptions implements DespotResponse {
    private final List<ResponseOption> options = new ArrayList<>();

    private Response defaultResponse = Response.noContent().build();

    public ResponseOptions add(Specification specification, DespotResponse despotResponse) {
        this.options.add(new ResponseOption(specification, despotResponse));
        return this;
    }

    @Override
    public Response response() {
        for (ResponseOption option : options) {
            if (option.specification.isTrue()) {
                return option.response.response();
            }
        }
        return defaultResponse;
    }

    private final class ResponseOption {
        public final Specification specification;
        public final DespotResponse response;

        private ResponseOption(Specification specification, DespotResponse response) {
            this.specification = specification;
            this.response = response;
        }
    }
}
