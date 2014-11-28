package de.itagile.despot;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;
import org.json.simple.parser.JSONParser;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Despot<ParamType> {

    private final List<DespotRoute> routes = new ArrayList<>();
    private final Set<Map> specs = new HashSet<>();
    private String endpoint = "/";
    private Method method = Method.GET;

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
        Map spec = new HashMap();
        spec.put(DespotSpecParser.URI, endpoint);
        spec.put(DespotSpecParser.METHOD, method.name());
        for (ResponseModifier modifier : modifiers) {
            modifier.spec(spec);
        }
        specs.add(spec);
        this.routes.add(new DespotRoute(specification, option, modifiers));
        return this;
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

    public Despot<ParamType> error(Class<? extends Exception> exception, ResponseModifier... responseModifiers) {
        return this;
    }

    public Response response(ParamType param) {
        for (DespotRoute element : routes) {
            Specification specification = element.specification.create(param);
            if (specification.isTrue()) {
                Response.ResponseBuilder responseBuilder = Response.noContent();
                DespotResponse response = element.response.create(param);
                response = response.modify(responseBuilder, response);
                for (ResponseModifier modifier : element.modifiers) {
                    response = modifier.modify(responseBuilder, response);
                }
                return responseBuilder.build();
            }
        }
        throw new IllegalStateException("No route matched and no fallback defined.");
    }

    public Despot<ParamType> verify(String path) {
        try {
            InputStream specStream = getClass().getResourceAsStream(path);
            Map spec = (Map) new JSONParser().parse(new InputStreamReader(specStream));
            boolean hasBeenVerified = verifyAllEndpoints(spec);
            if (!hasBeenVerified) {
                throw new IllegalStateException();
            }
            return this;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyAllEndpoints(Map spec) {
        List endpoints = (List) spec.get(DespotSpecParser.ENDPOINTS);
        Set<Map> sCopy = new HashSet<>(specs);
        Set<Map> jsonSpecs = new HashSet<Map>();
        for (Object element : endpoints) {
            Map endpoint = (Map) element;
            if (this.endpoint.equals(endpoint.get(DespotSpecParser.URI))) {
                return verifyAllMethods(endpoint, sCopy, jsonSpecs);
            }
        }
        return false;
    }

    private boolean verifyAllMethods(Map endpoint, Set<Map> sCopy, Set<Map> specs) {
        List methods = (List) endpoint.get(DespotSpecParser.METHODS);
        for (Object element : methods) {
            Map method = (Map) element;
            if (this.method.name().equals(method.get(DespotSpecParser.METHOD))) {
                return verifyAllStatusCodes(method, sCopy, specs);
            }
        }
        return false;
    }

    private boolean verifyAllStatusCodes(Map method, Set<Map> sCopy, Set<Map> jsonSpecs) {
        List allStatusCodes = (List) method.get(DespotSpecParser.RESPONSES);
        for (Object element : allStatusCodes) {
            Map spec = new HashMap();
            spec.put(DespotSpecParser.URI, endpoint);
            spec.put(DespotSpecParser.METHOD, this.method.name());
            Map statusCodeMap = (Map) element;
            String statusCodeString = statusCodeMap.get(DespotSpecParser.STATUS_CODE).toString();
            int statusCode = Integer.valueOf(statusCodeString);
            spec.put(DespotSpecParser.STATUS_CODE, statusCode);
            Object mediatype = statusCodeMap.get(DespotSpecParser.MEDIATYPE);
            if (mediatype != null) spec.put(DespotSpecParser.MEDIATYPE, mediatype);
            jsonSpecs.add(spec);
        }
        sCopy.removeAll(jsonSpecs);
        jsonSpecs.removeAll(this.specs);
        return sCopy.isEmpty(); // && jsonSpecs.isEmpty();
    }

    public static enum Method {
        GET, POST, PUT, DELETE;
    }

    public static class PreDespot<ParamType> extends Despot<ParamType> {

        private final SpecificationPartial<ParamType> s;

        private PreDespot(SpecificationPartial<ParamType> s) {
            this.s = s;
        }

        public PreDespot<ParamType> next(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, ResponseModifier... responseModifiers) {
            super.next(SpecificationPartial.and(s, specification, null), option, responseModifiers);
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