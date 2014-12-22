package de.itagile.despot.http;

import de.itagile.despot.ResponseModifier;
import de.itagile.model.Key;
import de.itagile.model.Model;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

public class LocationModifier implements ResponseModifier {
    private static final Key<String> KEY = new Key<String>() {
        @Override
        public String getUndefined() {
            throw new IllegalStateException("Location is mandatory!");
        }
    };
    private final String locationTemplate;
    private final String var;

    public LocationModifier(String locationTemplate, String var) {
        this.locationTemplate = locationTemplate;
        this.var = var;
    }

    public static ResponseModifier location(final String status, String var) {
        return new LocationModifier(status, var);
    }

    @Override
    public void spec(Map spec) {
        spec.put("location", locationTemplate);
    }

    @Override
    public void modify(Response.ResponseBuilder responseBuilder, Model model) throws Exception {
        String location = model.get(KEY);
        responseBuilder.location(URI.create(locationTemplate.replaceAll(var, location)));
    }

    @Override
    public String toString(){
        return "Location: " + locationTemplate;
    }
}
