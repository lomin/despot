package de.itagile.despot.http;

import de.itagile.despot.ResponseModifier;
import de.itagile.model.Key;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.*;

public class LocationModifier implements ResponseModifier {
    public static final Key<LocationBuilder> KEY = new Key<LocationBuilder>() {
        @Override
        public LocationBuilder getUndefined() {
            return new LocationBuilder() {
                @Override
                public URI build(Iterable<String> vars) {
                    throw new IllegalStateException();
                }

                @Override
                public LocationBuilder put(String var, String value) {
                    return this;
                }
            };
        }
    };
    private final Iterable<String> segmentVars;

    private LocationModifier(Iterable<String> segmentVars) {
        this.segmentVars = segmentVars;
    }

    public static ValidLocationBuilder locationBuilder(UriInfo uriInfo) {
        return new ValidLocationBuilder(uriInfo.getBaseUriBuilder());
    }

    public static ValidLocationBuilder locationBuilder(UriBuilder uriBuilder) {
        return new ValidLocationBuilder(uriBuilder);
    }

    public static ResponseModifier location(String[] vars, String... additionalVars) {
        List<String> segmentVars = new ArrayList<>();
        addNonEmpty(segmentVars, vars);
        addNonEmpty(segmentVars, additionalVars);
        return new LocationModifier(segmentVars);
    }

    public static ResponseModifier location(String first, String... additionalVars) {
        return location(new String[]{first}, additionalVars);
    }

    private static void addNonEmpty(List<String> segmentVars, String... vars) {
        for (String var : vars) {
            if (var != null && !var.trim().isEmpty()) {
                segmentVars.add(var);
            }
        }
    }

    @Override
    public void spec(Map<String, Object> spec) {
        String locationTemplate = "{base}";
        for (String segmentVar : segmentVars) {
            locationTemplate += "/" + segmentVar;
        }
        spec.put("location", locationTemplate);
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception {
        final LocationBuilder locationBuilder = model.get(KEY);
        responseBuilder.location(locationBuilder.build(segmentVars));
    }

    @Override
    public String toString() {
        return "Location: " + Arrays.asList(segmentVars);
    }

    public interface LocationBuilder {
        URI build(Iterable<String> vars);

        LocationBuilder put(String var, String value);
    }

    public static class ValidLocationBuilder implements LocationBuilder {

        private final Map<String, String> replacements = new HashMap<>();
        private final UriBuilder uriBuilder;

        private ValidLocationBuilder(UriBuilder uriBuilder) {
            this.uriBuilder = uriBuilder;
        }

        @Override
        public URI build(Iterable<String> vars) {
            UriBuilder builder = this.uriBuilder;
            for (String var : vars) {
                builder = builder.path(replace(var));
            }
            return builder.build();
        }

        @Override
        public LocationBuilder put(String var, String value) {
            replacements.put(var, value);
            return this;
        }

        private String replace(String var) {
            if (replacements.containsKey(var)) {
                return replacements.get(var);
            }
            return var;
        }
    }
}