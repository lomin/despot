package de.itagile.despot.http;

import de.itagile.model.HashModel;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;

import static de.itagile.despot.CollectionUtil.mapOf;
import static de.itagile.despot.http.LocationModifier.location;
import static de.itagile.despot.http.LocationModifier.locationBuilder;
import static org.junit.Assert.assertEquals;

public class LocationModifierTest {

    @Test
    public void replacesVarsInLocationBuilder() throws Exception {
        Response.ResponseBuilder responseBuilder = Response.noContent();

        location("import-status", "{r0}", "{r1}")
                .modify(responseBuilder, new HashModel().update(LocationModifier.KEY,
                        locationBuilder(UriBuilder.fromUri("http://host:1234")).put("{r0}", "3").put("{r1}", "4")));

        assertEquals(URI.create("http://host:1234/import-status/3/4"), responseBuilder.build().getMetadata().get("location").get(0));
    }

    @Test
    public void generatesSpec() throws Exception {
        HashMap spec = new HashMap();

        location("import-status", "{r0}", "{r1}")
                .spec(spec);

        assertEquals(mapOf("location", "{base}/import-status/{r0}/{r1}"), spec);
    }
}