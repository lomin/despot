package de.itagile.despot;

import de.itagile.mediatype.Format;
import de.itagile.mediatype.MediaType;
import de.itagile.model.Model;
import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;
import org.json.simple.parser.JSONParser;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Despot<ParamType> {

    private final List<DespotPartialElement> elements = new ArrayList<>();
    private final Map<Integer, EntityBuilder> entityBuilders = new HashMap<>();
    private final Set<Integer> allStatus = new HashSet<>();
    private String endpoint = "/";
    private Method method = Method.GET;
    private Map<Integer, MediaType<?, ? extends Format<?>>> statusMediaType = new HashMap<>();

    private Despot<ParamType> addOption(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, int status, List<ResponseModifier> modifiers) {
        this.elements.add(new DespotPartialElement(specification, option, status, modifiers));
        this.allStatus.add(status);
        return this;
    }

    public static interface ResponseModifier2<T> extends ResponseModifier {
        T get();
    }

    public Despot<ParamType> next(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, ResponseModifier2<Integer> modifier, ResponseModifier... responseModifiers) {
        List<ResponseModifier> modifiers = new ArrayList<>();
        modifiers.add(modifier);
        for (ResponseModifier r : responseModifiers) {
            modifiers.add(r);
        }
        return addOption(specification, option, modifier.get(), modifiers);
    }

    public Despot<ParamType> next(Despot<ParamType> preDespot) {
        for (DespotPartialElement element : preDespot.elements) {
            elements.add(element);
            this.allStatus.add(element.status);
        }
        return this;
    }

    public Despot<ParamType> last(ResponsePartial<ParamType> option, ResponseModifier2<Integer> modifier, ResponseModifier... responseModifiers) {
        List<ResponseModifier> modifiers = new ArrayList<>();
        modifiers.add(modifier);
        for (ResponseModifier r : responseModifiers) {
            modifiers.add(r);
        }
        return addOption(new SpecificationPartial<ParamType>() {
            @Override
            public Specification create(ParamType param) {
                return this;
            }

            @Override
            public boolean isTrue() {
                return true;
            }
        }, option, modifier.get(), modifiers);
    }

    public static <T> Despot<T> despot(String url, Method method, Class<T> _) {
        Despot<T> despot = new Despot<>();
        despot.endpoint = url;
        despot.method = method;
        return despot;
    }

    public static <ParamType> PreDespot<ParamType> pre(SpecificationPartial<ParamType> specification) {
        return new PreDespot<>(specification);
    }

    public Despot<ParamType> error(Class<? extends Exception> exception, int status, ResponseModifier... responseModifiers) {
        this.allStatus.add(status);
        return this;
    }

    public <T> Despot<ParamType>
    status(int status, final MediaType<T, ? extends Format<T>> mediaType, final EntityFactory<T> entityFactory) {
        if (!allStatus.contains(status)) {
            throw new RuntimeException("Not a declared status code: " + status);
        }
        statusMediaType.put(status, mediaType);
        entityBuilders.put(status, new EntityBuilder<>(entityFactory, mediaType));
        return this;
    }

    public Response response(ParamType param) {
        for (DespotPartialElement element : elements) {
            Specification specification = element.specification.create(param);
            if (specification.isTrue()) {
                Response.ResponseBuilder responseBuilder = Response.noContent();
                DespotResponse response = element.response.create(param);
                response = response.modify(responseBuilder, response);
                for(ResponseModifier modifier : element.modifiers) {
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
        for (Object element : endpoints) {
            Map endpoint = (Map) element;
            if (this.endpoint.equals(endpoint.get("uri"))) {
                return verifyAllMethods(endpoint);
            }
        }
        return false;
    }

    private boolean verifyAllMethods(Map endpoint) {
        List methods = (List) endpoint.get("methods");
        for (Object element : methods) {
            Map method = (Map) element;
            if (this.method.name().equals(method.get("method"))) {
                return verifyAllStatusCodes(method);
            }
        }
        return false;
    }

    private boolean verifyAllStatusCodes(Map method) {
        List allStatusCodes = (List) method.get("status_codes");
        Set<Integer> allowedStatusCodes = new HashSet<>();
        for (Object element : allStatusCodes) {
            Map statusCodeMap = (Map) element;
            String statusCodeString = statusCodeMap.get("status_code").toString();
            Integer statusCode = Integer.valueOf(statusCodeString);
            MediaType mediaType = statusMediaType.get(statusCode);
            if (mediaTypeNamesDiffer(statusCodeMap, mediaType) && !isRedirection(statusCode)) {
                return false;
            }
            allowedStatusCodes.add(statusCode);
        }
        return this.allStatus.equals(allowedStatusCodes);
    }

    private boolean mediaTypeNamesDiffer(Map statusCode, MediaType mediaType) {
        return mediaType == null || !mediaType.getName().equals(statusCode.get("mediatype"));
    }

    private boolean isRedirection(Integer status) {
        return status >= 300 && status < 400;
    }

    public static class PreDespot<ParamType> extends Despot<ParamType> {

        private final SpecificationPartial<ParamType> s;

        private PreDespot(SpecificationPartial<ParamType> s) {
            this.s = s;
        }

        public PreDespot<ParamType> next(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, ResponseModifier2<Integer> status, ResponseModifier... responseModifiers) {
            super.next(SpecificationPartial.and(s, specification, null), option, status, responseModifiers);
            return this;
        }
    }

    private static class EntityBuilder<T> {
        private final EntityFactory<T> entityFactory;
        private final MediaType<T, ? extends Format<T>> mediaType;

        private EntityBuilder(EntityFactory<T> entityFactory, MediaType<T, ? extends Format<T>> mediaType) {
            this.entityFactory = entityFactory;
            this.mediaType = mediaType;
        }
    }

    private class DespotPartialElement {
        private final SpecificationPartial<? super ParamType> specification;
        private final ResponsePartial<? super ParamType> response;
        private int status;
        private final List<ResponseModifier> modifiers;

        private DespotPartialElement(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> response, int status, List<ResponseModifier> modifiers) {
            this.specification = specification;
            this.response = response;
            this.status = status;
            this.modifiers = modifiers;
        }
    }

    public static enum Method {
        GET, POST, PUT, DELETE;
    }
}