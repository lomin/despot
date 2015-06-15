package de.itagile.despot.http;

import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ConsumesSpecifiedTest {

    @Test
    public void addsConsumes() throws Exception {
        final Map<String, Object> spec = new HashMap<>();

        ConsumesSpecified.consumes(MediaType.TEXT_HTML_TYPE).spec(spec);

        assertEquals("text/html", spec.get("consumes"));
    }
}