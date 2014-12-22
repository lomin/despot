package de.itagile.despot;

import de.itagile.model.HashModel;
import de.itagile.model.Model;
import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;
import org.json.simple.parser.ParseException;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

public class Despot<ParamType> {

    private final List<DespotRoute> routes = new ArrayList<>();
    private final Verifier verifier;
    private String endpoint = "/";
    private Method method = Method.GET;
    private DespotSpecParser specParser = new DespotSpecParser();
    private List<ErrorResponse> errorResponses = new ArrayList<>();

    public Despot(Verifier verifier, String uri, Method method) {
        this.verifier = verifier;
        this.endpoint = uri;
        this.method = method;
    }

    public Despot() {
        this.verifier = new DespotVerifier();
    }

    public static <T> Despot<T> despot(String uri, Method method, Class<T> _) {
        Despot<T> despot = new Despot<>();
        despot.endpoint = uri;
        despot.method = method;
        return despot;
    }

    public static <ParamType> PreDespot<ParamType> pre(SpecificationPartial<ParamType> specification) {
        return new PreDespot<>(specification);
    }

    private Despot<ParamType> addRoute(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, List<ResponseModifier> modifiers) {
        addSpec(modifiers);
        this.routes.add(new DespotRoute(specification, option, modifiers));
        return this;
    }

    private void addSpec(Iterable<ResponseModifier> modifiers) {
        Map<String, Object> spec = new HashMap<>();
        for (ResponseModifier modifier : modifiers) {
            modifier.spec(spec);
        }
        this.verifier.add(spec);
    }

    public Despot<ParamType> next(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, ResponseModifier... responseModifiers) {
        return addRoute(specification, option, Arrays.asList(responseModifiers));
    }

    public Despot<ParamType> next(Despot<ParamType> preDespot) {
        for (DespotRoute element : preDespot.routes) {
            addRoute(element.specification, element.response, element.modifiers);
        }
        return this;
    }

    public Despot<ParamType> last(ResponsePartial<ParamType> option, ResponseModifier... responseModifiers) {
        return addRoute(new SpecificationPartial<ParamType>() {
            @Override
            public Specification create(ParamType param) {
                return this;
            }

            @Override
            public boolean isTrue() {
                return true;
            }
        }, option, Arrays.asList(responseModifiers));
    }

    public Despot<ParamType> error(Class<? extends Exception> exception, ResponseModifier... modifiers) {
        List<ResponseModifier> responseModifiers = Arrays.asList(modifiers);
        addSpec(responseModifiers);
        this.errorResponses.add(new ErrorResponse(exception, responseModifiers));
        return this;
    }

    public Response response(ParamType param) {
        for (DespotRoute route : routes) {
            try {
                Specification specification = route.specification.create(param);
                if (specification.isTrue()) {
                    return buildResponse(route, param);
                }
            } catch (Exception e) {
                for (ErrorResponse errorResponse : errorResponses) {
                    if (errorResponse.exception.isAssignableFrom(e.getClass())) {
                        return buildErrorResponse(errorResponse);
                    }
                }
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException("No route matched and no fallback defined.");
    }

    private Response buildErrorResponse(ErrorResponse errorResponse) {
        Response.ResponseBuilder responseBuilder = Response.noContent();
        DespotResponse response = new EmptyResponse();
        for (ResponseModifier modifier : errorResponse.responseModifiers) {
            try {
                response = modifier.modify(responseBuilder, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return responseBuilder.build();
    }

    private Response buildResponse(DespotRoute element, ParamType param) throws Exception {
        Response.ResponseBuilder responseBuilder = Response.noContent();
        DespotResponse response = element.response.create(param);
        response = response.modify(responseBuilder, response);
        for (ResponseModifier modifier : element.modifiers) {
            response = modifier.modify(responseBuilder, response);
        }
        return responseBuilder.build();
    }

    public Despot<ParamType> verify(String path) {
        Set<Map<String, Object>> canonicalSpec;
        try {
            canonicalSpec = specParser.getSpec(method, endpoint, getClass().getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException("Could not find <" + path + "> on classpath.");
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse <" + path + "> as json.");
        }
        return verify(canonicalSpec);
    }

    public Despot<ParamType> verify(Set<Map<String, Object>> canonicalSpec) {
        DespotVerifier.Verifaction verifaction = verifier.verify(canonicalSpec);
        if (verifaction.verified()) {
            return this;
        }
        throw verifaction.exception();
    }

    public static enum Method {
        GET, POST, PUT, DELETE;
    }

    public static class PreDespot<ParamType> extends Despot<ParamType> {

        private final SpecificationPartial<ParamType> s;

        private PreDespot(SpecificationPartial<ParamType> s) {
            super();
            this.s = s;
        }

        public PreDespot<ParamType> next(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, ResponseModifier... responseModifiers) {
            super.next(SpecificationPartial.and(s, specification, null), option, responseModifiers);
            return this;
        }
    }

    private static class EmptyResponse implements DespotResponse {
        @Override
        public Model responseModel() {
            return new HashModel();
        }

        @Override
        public DespotResponse modify(Response.ResponseBuilder responseBuilder, DespotResponse despotResponse) {
            return despotResponse;
        }

        @Override
        public void spec(Map<String, Object> spec) {

        }
    }

    private class ErrorResponse {

        private final Class<? extends Exception> exception;
        private final List<ResponseModifier> responseModifiers;

        public ErrorResponse(Class<? extends Exception> exception, List<ResponseModifier> responseModifiers) {
            this.exception = exception;
            this.responseModifiers = responseModifiers;
        }
    }

    private class DespotRoute {
        private final SpecificationPartial<? super ParamType> specification;
        private final ResponsePartial<? super ParamType> response;
        private final List<ResponseModifier> modifiers;

        private DespotRoute(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> response, List<ResponseModifier> modifiers) {
            this.specification = specification;
            this.response = response;
            this.modifiers = modifiers;
        }
    }
}