package de.itagile.despot;

import de.itagile.model.Model;
import de.itagile.mediatype.Format;
import de.itagile.mediatype.MediaType;
import de.itagile.specification.Specification;
import de.itagile.specification.SpecificationPartial;

import javax.ws.rs.core.Response;
import java.util.*;

import static de.itagile.specification.SpecificationPartial.and;

public class Despot<ParamType> {

    private final List<DespotPartialElement> elements = new ArrayList<>();
    private final Map<Integer, EntityBuilder> entityBuilders = new HashMap<>();
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

    public static <T> Despot<T> first(SpecificationPartial<T> specification, ResponsePartial<T> option, int status) {
        return new Despot<T>().addOption(specification, option, status);
    }

    public static <ParamType> PreDespot<ParamType> pre(SpecificationPartial<ParamType> specification) {
        return new PreDespot<>(specification);
    }

    public Despot<ParamType> error(Class<? extends Exception> exception, int status) {
        return this;
    }

    public <T> Despot<ParamType> status(int status, final MediaType<? extends Format<T>> mediaType, final EntityFactory<T> entityFactory) {
        entityBuilders.put(status, new EntityBuilder<>(entityFactory, mediaType));
        return this;
    }

    public Response response(ParamType param) {
        for (DespotPartialElement element : elements) {
            Specification specification = element.specification.create(param);
            if (specification.isTrue()) {
                int status = element.status;
                Model model = element.response.responseModel();
                EntityBuilder entityBuilder = entityBuilders.get(status);
                Object entity = entityBuilder.transform(model);
                Response.ResponseBuilder responseBuilder = Response.status(status).entity(entity);
                element.response.modify(responseBuilder);
                return responseBuilder.build();
            }
        }
        throw new IllegalStateException();
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

    public static <T> void transform(Model source, T target, MediaType<? extends Format<T>> mediaType) {
        for (Format<T> key : mediaType) {
            key.transform(source, target);
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
        private final SpecificationPartial<ParamType> specification;
        private final ResponsePartial<ParamType> response;
        private int status;

        private DespotPartialElement(SpecificationPartial<ParamType> specification, ResponsePartial<ParamType> response, int status) {
            this.specification = specification;
            this.response = response;
            this.status = status;
        }
    }
}