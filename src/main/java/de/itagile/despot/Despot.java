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
    private Map<Integer, MediaType<? extends Format<?>>> statusMediaType = new HashMap<>();

    public static <T> void transform(Model source, T target, MediaType<? extends Format<T>> mediaType) {
        for (Format<T> key : mediaType) {
            key.transform(source, target);
        }
    }

    private Despot<ParamType> addOption(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, int status) {
        this.elements.add(new DespotPartialElement(specification, option, status));
        this.allStatus.add(status);
        return this;
    }

    public Despot<ParamType> next(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, int status) {
        return addOption(specification, option, status);
    }

    public Despot<ParamType> next(Despot<ParamType> preDespot) {
        for (DespotPartialElement element : preDespot.elements) {
            elements.add(element);
            this.allStatus.add(element.status);
        }
        return this;
    }

    public Despot<ParamType> last(ResponsePartial<ParamType> option, int status) {
        return addOption(new SpecificationPartial<ParamType>() {
            @Override
            public Specification create(ParamType param) {
                return this;
            }

            @Override
            public boolean isTrue() {
                return true;
            }
        }, option, status);
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

    public Despot<ParamType> error(Class<? extends Exception> exception, int status) {
        this.allStatus.add(status);
        return this;
    }

    public <T> Despot<ParamType>
    status(int status, final MediaType<? extends Format<T>> mediaType, final EntityFactory<T> entityFactory) {
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
                int status = element.status;
                DespotResponse response = element.response.create(param);
                Model model = response.responseModel();
                EntityBuilder entityBuilder = entityBuilders.get(status);
                Object entity = null;
                if (entityBuilder != null) {
                    entity = entityBuilder.transform(model);
                }
                Response.ResponseBuilder responseBuilder = Response.status(status).entity(entity);
                response.modify(responseBuilder);
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

        public PreDespot<ParamType> next(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> option, int status) {
            super.next(SpecificationPartial.and(s, specification, null), option, status);
            return this;
        }
    }

    private static class EntityBuilder<T> {
        private final EntityFactory<T> entityFactory;
        private final MediaType<? extends Format<T>> mediaType;

        private EntityBuilder(EntityFactory<T> entityFactory, MediaType<? extends Format<T>> mediaType) {
            this.entityFactory = entityFactory;
            this.mediaType = mediaType;
        }

        private T transform(Model source) {
            T entity = entityFactory.create();
            Despot.transform(source, entity, mediaType);
            return entity;
        }
    }

    private class DespotPartialElement {
        private final SpecificationPartial<? super ParamType> specification;
        private final ResponsePartial<? super ParamType> response;
        private int status;

        private DespotPartialElement(SpecificationPartial<? super ParamType> specification, ResponsePartial<? super ParamType> response, int status) {
            this.specification = specification;
            this.response = response;
            this.status = status;
        }
    }

    public static enum Method {
        GET, POST, PUT, DELETE;
    }
}