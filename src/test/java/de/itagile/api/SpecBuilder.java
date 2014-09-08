package de.itagile.api;

import de.itagile.despot.Despot;

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
        if (!model.containsKey("endpoint")) {
            model.put("endpoints", new ArrayList<>());
        }
        List endpoints = (List) model.get("endpoints");
        endpoints.add(endpoint.build());
        return this;
    }

    static class EndpointBuilder extends Builder {

        private EndpointBuilder() {
        }

        EndpointBuilder uri(String uri) {
            model.put("uri", uri);
            return this;
        }

        EndpointBuilder addMethod(MethodBuilder method) {
            if (!model.containsKey("methods")) {
                model.put("methods", new ArrayList<>());
            }
            List methods = (List) model.get("methods");
            methods.add(method.build());
            return this;
        }
    }

    static class MethodBuilder extends Builder {

        private MethodBuilder() {
            super();
            model.put("status_codes", new ArrayList<>());
        }

        MethodBuilder method(Despot.Method method) {
            model.put("method", method.name());
            return this;
        }

        MethodBuilder addStatusCode(int code, String mediatype) {
            List status_codes = (List) model.get("status_codes");
            HashMap statusCode = new HashMap();
            statusCode.put("status_code", code);
            statusCode.put("mediatype", mediatype);
            status_codes.add(statusCode);
            return this;
        }
    }
}
