package de.itagile.despot;

import org.junit.Test;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static de.itagile.despot.CollectionUtil.mapOf;
import static de.itagile.despot.CollectionUtil.setOf;
import static de.itagile.mediatype.MediaType.consumes;
import static de.itagile.mediatype.MediaType.consumesNone;
import static org.junit.Assert.assertEquals;

public class DespotSpecParserTest {

    private final DespotSpecParser parser = new DespotSpecParser();

    @Test
    public void returnsAllResponseCombinationIfEndpointAndMethodMatchesAndConsumes() throws Exception {
        Set<Map<String, Object>> spec = parser.getSpec(Method.GET, "/items/{path}", consumes("application/x-www-form-urlencoded"), getStream());

        assertEquals(5, spec.size());
    }

    @Test
    public void returnsNoResponseCombinationIfEndpointDoesNotMatch() throws Exception {
        Set<Map<String, Object>> spec = parser.getSpec(Method.GET, "/UNKNOWN", consumes("application/x-www-form-urlencoded"), getStream());

        assertEquals(0, spec.size());
    }

    @Test
    public void returnsNoResponseCombinationIfMethodtDoesNotMatch() throws Exception {
        Set<Map<String, Object>> spec = parser.getSpec(Method.DELETE, "/items/{path}", consumesNone(), getStream());

        assertEquals(0, spec.size());
    }

    @Test
    public void returnsResponseCombinationsForExpandedMediaType() throws Exception {
        Set<Map<String, Object>> spec = parser.getSpec(Method.POST, "/items/{path}", consumes("application/vnd.itagile.product+json"), getStream());

        assertEquals(1, spec.size());
    }

    @Test
    public void expandsMediaTypeInSpec() throws Exception {
        Set<Map<String, Object>> spec = parser.getSpec(Method.GET, "/NOT_ALLOWED", consumesNone(), getStream());

        assertEquals(mapOf(
                "method", "GET",
                "uri", "/NOT_ALLOWED",
                "method", "GET",
                "consumes", "NONE",
                "status_code", "405",
                "produces", mapOf(
                        "name", "application/vnd.itagile.error+json",
                        "fields", setOf(
                                mapOf(
                                        "name", "reason",
                                        "type", mapOf("name", "String"))
                        ))), spec.iterator().next());

    }

    @Test
    public void normalizesMap() throws Exception {
        Map<String, Object> input = mapOf(
                "method", "GET",
                "status_code", 405L,
                "produces", mapOf(
                        "name", "application/vnd.itagile.error+json",
                        "fields", setOf(
                                mapOf(
                                        "params", setOf(1, 2, 3),
                                        "type", mapOf("name", "String"))
                        )));
        Map<String, Object> expected = mapOf(
                "method", "GET",
                "status_code", "405",
                "produces", mapOf(
                        "name", "application/vnd.itagile.error+json",
                        "fields", setOf(
                                mapOf(
                                        "params", setOf("1", "2", "3"),
                                        "type", mapOf("name", "String"))
                        )));

        assertEquals(expected, parser.normalize(input));
    }

    @Test
    public void expandsMap() throws Exception {
        Map<String, Object> input =
                mapOf(
                        "endpoints", setOf(
                                mapOf(
                                        "uri", "/items/{path}",
                                        "methods", setOf(mapOf(
                                                        "__description__", "ignore",
                                                        "method", "GET",
                                                        "consumes", "application/x-www-form-urlencoded",
                                                        "responses", setOf(
                                                                mapOf(
                                                                        "status-code", "200",
                                                                        "produces", "application/vnd.itagile.product+json",
                                                                        "__description__", "ignore"),
                                                                mapOf(
                                                                        "status-code", "301",
                                                                        "__description__", "ignore",
                                                                        "location", "{base}/items/{path}",
                                                                        "max-age", mapOf(
                                                                                "duration", "5",
                                                                                "time-unit", "SECONDS"))))
                                        )),
                                mapOf(
                                        "uri", "/items2/{path}",
                                        "methods", setOf(mapOf(
                                                        "__description__", "ignore",
                                                        "method", "GET",
                                                        "consumes", "application/x-www-form-urlencoded",
                                                        "responses", setOf(
                                                                mapOf(
                                                                        "status-code", "200",
                                                                        "produces", "application/vnd.itagile.product+json",
                                                                        "__description__", "ignore"),
                                                                mapOf(
                                                                        "status-code", "301",
                                                                        "__description__", "ignore",
                                                                        "location", "{base}/items/{path}",
                                                                        "max-age", mapOf(
                                                                                "duration", "5",
                                                                                "time-unit", "SECONDS"))))
                                        ))));

        Set<Map<String, Object>> expected = setOf(
                mapOf(
                        "uri", "/items/{path}",
                        "method", "GET",
                        "consumes", "application/x-www-form-urlencoded",
                        "status-code", "200",
                        "produces", "application/vnd.itagile.product+json"),
                mapOf(
                        "uri", "/items/{path}",
                        "method", "GET",
                        "consumes", "application/x-www-form-urlencoded",
                        "status-code", "301",
                        "location", "{base}/items/{path}",
                        "max-age", mapOf(
                                "duration", "5",
                                "time-unit", "SECONDS")),
                mapOf(
                        "uri", "/items2/{path}",
                        "method", "GET",
                        "consumes", "application/x-www-form-urlencoded",
                        "status-code", "200",
                        "produces", "application/vnd.itagile.product+json"),
                mapOf(
                        "uri", "/items2/{path}",
                        "method", "GET",
                        "consumes", "application/x-www-form-urlencoded",
                        "status-code", "301",
                        "location", "{base}/items/{path}",
                        "max-age", mapOf(
                                "duration", "5",
                                "time-unit", "SECONDS")));

        assertEquals(expected, parser.expand(input, Collections.emptyMap()));
    }

    private InputStream getStream() {
        return getClass().getResourceAsStream("/de.itagile.spec/spec.json");
    }
}