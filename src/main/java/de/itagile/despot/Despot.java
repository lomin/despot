package de.itagile.despot;

import de.itagile.despot.http.MethodSpecified;
import de.itagile.model.HashModel;
import de.itagile.model.Key;
import de.itagile.model.Model;
import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;
import org.json.simple.parser.ParseException;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

public class Despot<ParamType> {

    public static final Key<Exception> LAST_EXCEPTION = new Key<Exception>() {
        @Override
        public Exception getUndefined() {
            return new Exception();
        }
    };
    private final List<DespotRoute> routes = new ArrayList<>();
    private final Verifier verifier;
    private List<Specified> additionalSpecifications = new ArrayList<>();
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

    public static <T> Despot<T> despot(Class<T> _, String uri, Method method, Specified... additionalSpecification) {
        Despot<T> despot = new Despot<>();
        despot.endpoint = uri;
        despot.method = method;
        despot.additionalSpecifications.addAll(Arrays.asList(additionalSpecification));
        despot.additionalSpecifications.add(MethodSpecified.method(method));
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
        for (Specified specification : additionalSpecifications) {
            specification.spec(spec);
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

    public Despot<ParamType> last(ResponsePartial<? super ParamType> option, ResponseModifier... responseModifiers) {
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
        return error(exception, new NOPResponsePartial(), modifiers);
    }

    public Despot<ParamType> error(Class<? extends Exception> exception, ResponsePartial<? super ParamType> responsePartial, ResponseModifier... modifiers) {
        List<ResponseModifier> responseModifiers = Arrays.asList(modifiers);
        addSpec(responseModifiers);
        this.errorResponses.add(new ErrorResponse(exception, responsePartial, responseModifiers));
        return this;
    }

    public Response response(ParamType param) {
        HashModel model = new HashModel();
        for (DespotRoute route : routes) {
            try {
                Specification specification = route.specification.create(param);
                if (specification.isTrue()) {
                    return buildResponse(route, param, model);
                }
            } catch (Exception e) {
                model.update(LAST_EXCEPTION, e);
                for (ErrorResponse errorResponse : errorResponses) {
                    if (errorResponse.exception.isAssignableFrom(e.getClass())) {
                        return errorResponse.buildResponse(model, param);
                    }
                }
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException("No route matched and no fallback defined.");
    }

    private Response buildResponse(DespotRoute element, ParamType param, HashModel model) throws Exception {
        Response.ResponseBuilder responseBuilder = Response.noContent();
        ResponseModifier response = element.response.create(param);
        response.modify(responseBuilder, model);
        for (ResponseModifier modifier : element.modifiers) {
            modifier.modify(responseBuilder, model);
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
        Verification verification = verifier.verify(canonicalSpec);
        if (verification.verified()) {
            return this;
        }
        throw verification.exception();
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

    private class ErrorResponse {

        private final Class<? extends Exception> exception;
        private final List<ResponseModifier> responseModifiers = new ArrayList<>();
        private final ResponsePartial<? super ParamType> responsePartial;

        private ErrorResponse(Class<? extends Exception> exception, ResponsePartial<? super ParamType> responsePartial, List<ResponseModifier> responseModifiers) {
            this.exception = exception;
            this.responsePartial = responsePartial;
            this.responseModifiers.addAll(responseModifiers);
        }

        private Response buildResponse(Model model, ParamType param) {
            Response.ResponseBuilder responseBuilder = Response.noContent();
            responseModifiers.add(this.responsePartial.create(param));
            for (ResponseModifier modifier : responseModifiers) {
                try {
                    modifier.modify(responseBuilder, model);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return responseBuilder.build();
        }


    }

    private class NOPResponsePartial extends ResponsePartial<ParamType> {
        @Override
        public ResponseModifier create(ParamType param) {
            return this;
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