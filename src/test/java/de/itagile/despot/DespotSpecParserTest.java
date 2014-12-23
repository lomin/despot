package de.itagile.despot;

import org.junit.Test;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static de.itagile.util.CollectionUtil.mapOf;
import static de.itagile.util.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;

public class DespotSpecParserTest {

    private final DespotSpecParser parser = new DespotSpecParser();

    @Test
    public void returnsAllResponseCombinationIfEndpointAndMethodMatches() throws Exception {
        Set<Map<String, Object>> spec = parser.getSpec(Method.GET, "/items/{path}", getStream());

        assertEquals(5, spec.size());
    }

    @Test
    public void returnsNoResponseCombinationIfEndpointDoesNotMatch() throws Exception {
        Set<Map<String, Object>> spec = parser.getSpec(Method.GET, "/UNKNOWN", getStream());

        assertEquals(0, spec.size());
    }

    @Test
    public void returnsNoResponseCombinationIfMethodtDoesNotMatch() throws Exception {
        Set<Map<String, Object>> spec = parser.getSpec(Method.POST, "/items/{path}", getStream());

        assertEquals(0, spec.size());
    }

    @Test
    public void expandsMediaTypeInSpec() throws Exception {
        Set<Map<String, Object>> spec = parser.getSpec(Method.GET, "/NOT_ALLOWED", getStream());

        assertEquals(mapOf(
                "method", "GET",
                "status_code", 405L,
                "produces", mapOf(
                        "name", "application/vnd.itagile.error+json",
                        "fields", setOf(
                                mapOf(
                                        "name", "reason",
                                        "type", mapOf("name", "String"))
                        ))), spec.iterator().next());

    }

    private InputStream getStream() {
        return getClass().getResourceAsStream("/de.itagile.spec/spec.json");
    }
}