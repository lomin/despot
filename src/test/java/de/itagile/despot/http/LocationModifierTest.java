package de.itagile.despot.http;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import de.itagile.despot.ResponseModifier;
import de.itagile.model.HashModel;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static de.itagile.despot.http.LocationModifier.location;
import static org.junit.Assert.assertEquals;

public class LocationModifierTest {

    @Test
    public void replacesEvenExpressionUsedInRegexes() throws Exception {
        Response.ResponseBuilder responseBuilder = Response.noContent();
        List<String> replacements = new ArrayList<>();
        replacements.add("localhost");
        replacements.add("3");
        location("{basePath}/import-status/{job-id}/{job-id}", "{basePath}", "{job-id}")
                .modify(responseBuilder, new HashModel().update(LocationModifier.KEY, replacements));

        assertEquals(URI.create("localhost/import-status/3/3"), responseBuilder.build().getMetadata().get("location").get(0));
    }
}