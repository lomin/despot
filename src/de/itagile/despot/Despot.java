package de.itagile.despot;

import de.itagile.ces.Entity;
import de.itagile.mediatype.*;
import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

import javax.ws.rs.core.Response;
import java.util.*;

import static de.itagile.specification.SpecificationPartial.and;

public class Despot<ParamType> {

    private final List<DespotPartialElement> elements = new ArrayList<>();

    private Despot<ParamType> addOption(SpecificationPartial<ParamType> specification, ResponsePartial<ParamType> option, int status) {
        this.elements.add(new DespotPartialElement(specification, option, status));
        return this;
    }

    public Despot<ParamType> next(SpecificationPartial<ParamType> specification, ResponsePartial<ParamType> option, int status) {
        return addOption(specification, option, status);
    }

    public Despot<ParamType> next(Despot<ParamType> preDespot) {
        for(DespotPartialElement element: preDespot.elements) {
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

        public Response response(ParamType param,  ResponseType result) {
            for (DespotPartialElement element : elements) {
                Specification specification = element.specification.create(param);
                if (specification.isTrue()) {
                    Entity entity =  element.response.response2();
                    int status = element.status;
                    Set<MediaType> mediaType = types.get(status);
                    for (Format<ResponseType> key : mediaType) {
                        key.put(entity, result);
                    }
                    Response.ResponseBuilder responseBuilder = getResponseBuilderByStatus(status, result);
                    element.response.modify(responseBuilder);
                    return responseBuilder.build();
                }
            }
            throw new IllegalStateException();
        }

        private Response.ResponseBuilder getResponseBuilderByStatus(int status, ResponseType entity) {
            if (status == 200) {
                return Response.ok(entity);
            } else {
                return Response.status(status).entity(entity);
            }
        }
    }
}