package de.itagile.despot;

import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;
import org.json.simple.parser.JSONParser;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Despot<ParamType> {

    private final List<DespotPartialElement> elements = new ArrayList<>();
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

    private Despot<ParamType> addOption(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, List<ResponseModifier> modifiers) {
        Map spec = new HashMap();
        spec.put("uri", endpoint);
        spec.put("method", method.name());
        for (ResponseModifier modifier : modifiers) {
            modifier.spec(spec);
        }
        specs.add(spec);
        this.elements.add(new DespotPartialElement(specification, option, modifiers));
        return this;
    }

    public Despot<ParamType> next(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, ResponseModifier... responseModifiers) {
        return addOption(specification, option, Arrays.asList(responseModifiers));
    }

    public Despot<ParamType> next(Despot<ParamType> preDespot) {
        for (DespotPartialElement element : preDespot.elements) {
            addOption(element.specification, element.response, element.modifiers);
        }
        return this;
    }

    public Despot<ParamType> last(ResponsePartial<ParamType> option, ResponseModifier... responseModifiers) {
        return addOption(new SpecificationPartial<ParamType>() {
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
        for (DespotPartialElement element : elements) {
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
        throw new IllegalStateException();
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
        List endpoints = (List) spec.get("endpoints");
        Set<Map> sCopy = new HashSet<>(specs);
        for (Object element : endpoints) {
            Map endpoint = (Map) element;
            if (this.endpoint.equals(endpoint.get("uri"))) {
                return verifyAllMethods(endpoint, sCopy);
            }
        }
        return false;
    }

    private boolean verifyAllMethods(Map endpoint, Set<Map> sCopy) {
        List methods = (List) endpoint.get("methods");
        for (Object element : methods) {
            Map method = (Map) element;
            if (this.method.name().equals(method.get("method"))) {
                return verifyAllStatusCodes(method, sCopy);
            }
        }
        return false;
    }

    private boolean verifyAllStatusCodes(Map method, Set<Map> sCopy) {
        List allStatusCodes = (List) method.get("status_codes");
        for (Object element : allStatusCodes) {
            Map spec = new HashMap();
            spec.put("uri", endpoint);
            spec.put("method", this.method.name());
            Map statusCodeMap = (Map) element;
            String statusCodeString = statusCodeMap.get("status_code").toString();
            int statusCode = Integer.valueOf(statusCodeString);
            spec.put("status_code", statusCode);
            Object mediatype = statusCodeMap.get("mediatype");
            if (mediatype != null) spec.put("mediatype", mediatype);
            sCopy.remove(spec);
        }
        return sCopy.isEmpty();
    }

    public static enum Method {
        GET, POST, PUT, DELETE;
    }

    public static interface ResponseModifier2<T> extends ResponseModifier {
        T get();
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

    private class DespotPartialElement {
        private final SpecificationPartial<? super ParamType> specification;
        private final ResponsePartial<? super ParamType> response;
        private final List<ResponseModifier> modifiers;

        private DespotPartialElement(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> response, List<ResponseModifier> modifiers) {
            this.specification = specification;
            this.response = response;
            this.modifiers = modifiers;
        }
    }
}