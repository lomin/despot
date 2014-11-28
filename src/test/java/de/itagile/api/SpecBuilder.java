package de.itagile.api;

import de.itagile.despot.Despot;
import de.itagile.despot.DespotSpecParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpecBuilder extends Builder {

    private SpecBuilder() {
    }

    static EndpointBuilder endpoint() {
        return new EndpointBuilder();
    }

    public static SpecBuilder spec() {
        return new SpecBuilder();
    }

    static MethodBuilder method() {
        return new MethodBuilder();
    }

    SpecBuilder addEndpoint(EndpointBuilder endpoint) {
        if (!model.containsKey(DespotSpecParser.ENDPOINTS)) {
            model.put(DespotSpecParser.ENDPOINTS, new ArrayList<>());
        }
        List endpoints = (List) model.get(DespotSpecParser.ENDPOINTS);
        endpoints.add(endpoint.build());
        return this;
    }

    static class EndpointBuilder extends Builder {

        private EndpointBuilder() {
        }

        EndpointBuilder uri(String uri) {
            model.put(DespotSpecParser.URI, uri);
            return this;
        }

        EndpointBuilder addMethod(MethodBuilder method) {
            if (!model.containsKey(DespotSpecParser.METHODS)) {
                model.put(DespotSpecParser.METHODS, new ArrayList<>());
            }
            List methods = (List) model.get(DespotSpecParser.METHODS);
            methods.add(method.build());
            return this;
        }
    }

    static class MethodBuilder extends Builder {

        private MethodBuilder() {
            super();
            model.put(DespotSpecParser.RESPONSES, new ArrayList<>());
        }

        MethodBuilder method(Despot.Method method) {
            model.put(DespotSpecParser.METHOD, method.name());
            return this;
        }

        MethodBuilder addStatusCode(int code, String mediatype) {
            List status_codes = (List) model.get(DespotSpecParser.RESPONSES);
            HashMap statusCode = new HashMap();
            statusCode.put(DespotSpecParser.STATUS_CODE, code);
            statusCode.put(DespotSpecParser.MEDIATYPE, mediatype);
            status_codes.add(statusCode);
            return this;
        }
    }
}
