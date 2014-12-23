package de.itagile.despot.http;

import de.itagile.despot.ResponseModifier;
import de.itagile.model.Key;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static java.util.regex.Pattern.quote;

public class LocationModifier implements ResponseModifier {
    public static final Key<Iterable<String>> KEY = new Key<Iterable<String>>() {
        @Override
        public Iterable<String> getUndefined() {
            return Collections.emptyList();
        }
    };
    private final String locationTemplate;
    private final String[] vars;

    public LocationModifier(String locationTemplate, String... vars) {
        this.locationTemplate = locationTemplate;
        this.vars = vars;
    }

    public static ResponseModifier location(final String locationTemplate, String... vars) {
        return new LocationModifier(locationTemplate, vars);
    }

    @Override
    public void spec(Map spec) {
        spec.put("location", locationTemplate);
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception {
        Iterable<String> locations = model.get(KEY);
        String path = locationTemplate;
        int i = 0;
        for (String location : locations) {
            if (i >= vars.length) break;
            path = path.replaceAll(quote(vars[i++]), location);
        }
        responseBuilder.location(URI.create(path));
    }

    @Override
    public String toString(){
        return "Location: " + locationTemplate;
    }
}
