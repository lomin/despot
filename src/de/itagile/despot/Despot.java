package de.itagile.despot;

import de.itagile.ces.Entity;
import de.itagile.mediatype.Format;
import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

import javax.ws.rs.core.Response;
import java.util.*;

import static de.itagile.specification.SpecificationPartial.and;

public class Despot<ParamType> {

    private final List<DespotPartialElement> elements = new ArrayList<>();
    private final Set<Integer> allStatus = new HashSet<>();

    private Despot<ParamType> addOption(SpecificationPartial<ParamType> specification, ResponsePartial<ParamType> option, int status) {
        this.elements.add(new DespotPartialElement(specification, option, status));
        this.allStatus.add(status);
        return this;
    }

    public Despot<ParamType> next(SpecificationPartial<ParamType> specification, ResponsePartial<ParamType> option, int status) {
        return addOption(specification, option, status);
    }

    public Despot<ParamType> next(Despot<ParamType> preDespot) {
        for (DespotPartialElement element : preDespot.elements) {
            elements.add(element);
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

    public <ResponseType, MediaType extends Format<ResponseType>> MediaTyped mediaType(Class<ResponseType> responseClass, int status, Set<MediaType> mediaType) {
        return new MediaTyped<ResponseType, MediaType>().mediaType(status, mediaType);
    }

    public static <T> Despot<T> first(SpecificationPartial<T> specification, ResponsePartial<T> option, int status) {
        return new Despot<T>().addOption(specification, option, status);
    }

    public static <ParamType> PreDespot<ParamType> pre(SpecificationPartial<ParamType> specification) {
        return new PreDespot<>(specification);
    }

    public Despot<ParamType> error(Class<? extends Exception> exception, int status) {
        return this;
    }

    private interface Serializer<T> {
        Object serialize(Entity e);
    }

    public static interface Recreatable<T> {
        T recreate();
    }

    Map<Integer, Serializer> serializers = new HashMap<>();

    public <T> Despot status(int status, final Set<? extends Format<T>> mediaType, final Recreatable<T> recreatable) {
        serializers.put(status, new Serializer<T>() {
            @Override
            public Object serialize(Entity e) {
                T result = recreatable.recreate();
                for (Format<T> key : mediaType) {
                    key.serialize(e, result);
                }
                return result;
            }
        });
        return this;
    }

    public Response response(ParamType param) {
        for (DespotPartialElement element : elements) {
            Specification specification = element.specification.create(param);
            if (specification.isTrue()) {
                int status = element.status;
                Entity entity = element.response.response2();
                Serializer serializer = serializers.get(status);
                Object result = serializer.serialize(entity);
                Response.ResponseBuilder responseBuilder = getResponseBuilderByStatus(status, result);
                element.response.modify(responseBuilder);
                return responseBuilder.build();
            }
        }
        throw new IllegalStateException();
    }

    private Response.ResponseBuilder getResponseBuilderByStatus(int status, Object entity) {
        if (status == 200) {
            return Response.ok(entity);
        } else {
            return Response.status(status).entity(entity);
        }
    }


    public static class PreDespot<ParamType> extends Despot<ParamType> {

        private final SpecificationPartial<ParamType> s;

        private PreDespot(SpecificationPartial<ParamType> s) {
            this.s = s;
        }

        public PreDespot<ParamType> next(SpecificationPartial<ParamType> specification, ResponsePartial<ParamType> option, int status) {
            super.next(and(s, specification), option, status);
            return this;
        }
    }

    private class DespotPartialElement {
        private final SpecificationPartial<ParamType> specification;
        private final ResponsePartial<ParamType> response;
        private int status;

        private DespotPartialElement(SpecificationPartial<ParamType> specification, ResponsePartial<ParamType> response, int status) {
            this.specification = specification;
            this.response = response;
            this.status = status;
        }
    }

    public class MediaTyped<ResponseType, MediaType extends Format<ResponseType>> {

        private final Map<Integer, Set<MediaType>> types = new HashMap<>();

        public MediaTyped<ResponseType, MediaType> mediaType(int status, Set<MediaType> mediaType) {
            types.put(status, mediaType);
            return this;
        }

        public Response response(ParamType param, ResponseType result) {
            for (DespotPartialElement element : elements) {
                Specification specification = element.specification.create(param);
                if (specification.isTrue()) {
                    int status = element.status;
                    Entity entity = element.response.response2();
                    Set<MediaType> mediaType = types.get(status);
                    serialize(entity, result, mediaType);
                    Response.ResponseBuilder responseBuilder = getResponseBuilderByStatus(status, result);
                    element.response.modify(responseBuilder);
                    return responseBuilder.build();
                }
            }
            throw new IllegalStateException();
        }

        public void serialize(Entity entity, ResponseType result, Set<MediaType> mediaType) {
            for (Format<ResponseType> key : mediaType) {
                key.serialize(entity, result);
            }
        }

        private Response.ResponseBuilder getResponseBuilderByStatus(int status, Object entity) {
            if (status == 200) {
                return Response.ok(entity);
            } else {
                return Response.status(status).entity(entity);
            }
        }
    }
}