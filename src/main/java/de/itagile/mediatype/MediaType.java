package de.itagile.mediatype;

import de.itagile.despot.DespotResponse;
import de.itagile.despot.DespotSpecParser;
import de.itagile.despot.EntityFactory;
import de.itagile.despot.ResponseModifier;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.util.*;

public class MediaType<T, FormatType extends Format<T>> implements Iterable<FormatType>, EntityFactory<T>, ResponseModifier {

    private final Set<FormatType> mediaTypes = new HashSet<>();
    private final String name;
    private final EntityFactory<T> entityFactory;

    public MediaType(String name, EntityFactory<T> entityFactory, FormatType... types) {
        this.name = name;
        this.entityFactory = entityFactory;
        this.mediaTypes.addAll(Arrays.asList(types));
    }

    public T modify(Model source) {
        T target = create();
        for (Format<T> key : this) {
            key.transform(source, target);
        }
        return target;
    }

    public Iterator<FormatType> iterator() {
        return new Iterator<FormatType>() {
            private final Iterator<FormatType> iterator = mediaTypes.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public FormatType next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public String getName() {
        return name;
    }

    public Map getSpec() {
        Map spec = new HashMap();
        spec.put("name", this.name);
        Set fields = new HashSet();
        spec.put("fields", fields);
        for (FormatType format : mediaTypes) {
            Map subSpec = new HashMap();
            format.spec(subSpec);
            fields.add(subSpec);
        }
        return spec;
    }

    @Override
    public String toString() {
        return "MediaType{" +
                "name='" + this.name + '\'' +
                '}';
    }

    @Override
    public T create() {
        return this.entityFactory.create();
    }

    @Override
    public DespotResponse modify(Response.ResponseBuilder responseBuilder, DespotResponse despotResponse) throws Exception {
        T entity = modify(despotResponse.responseModel());
        responseBuilder.entity(entity);
        return despotResponse;
    }

    @Override
    public void spec(Map<String, Object> spec) {
        spec.put(DespotSpecParser.MEDIATYPE, getSpec());
    }
}
