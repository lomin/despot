package de.itagile.despot;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class DespotSpecParser {

    public static final String RESPONSES = "responses";
    public static final String METHODS = "methods";
    public static final String METHOD = "method";
    public static final String STATUS_CODE = "status_code";
    public static final String MEDIATYPE = "mediatype";
    public static final String URI = "uri";
    public static final String ENDPOINTS = "endpoints";

    public Set<Map> getSpec(String path, Despot.Method method, String uri) {
        try {
            InputStream specStream = getClass().getResourceAsStream(path);
            Map completeSpec = (Map) new JSONParser().parse(new InputStreamReader(specStream));
            HashSet<Map> result = new HashSet<>();
            visitAllEndpoints(method, uri, completeSpec, result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Could not find <" + path + "> on classpath.");
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse <" + path + "> as json.");
        }
    }

    public void visitAllEndpoints(Despot.Method method, String uri, Map completeSpec, Set<Map> result) {
        List endpoints = (List) completeSpec.get(ENDPOINTS);
        for (Object element : endpoints) {
            Map endpointMap = (Map) element;
            if (uri.equals(endpointMap.get(URI))) {
                visitAllMethods(method, endpointMap, result);
            }
        }
    }

    private void visitAllMethods(Despot.Method method, Map endpointMap, Set<Map> result) {
        List methods = (List) endpointMap.get(METHODS);
        for (Object element : methods) {
            Map methodMap = (Map) element;
            if (method.name().equals(methodMap.get(METHOD))) {
                visitAllResponses(methodMap, result);
            }
        }
    }

    private void visitAllResponses(Map methodMap, Set<Map> result) {
        List allStatusCodes = (List) methodMap.get(RESPONSES);
        for (Object element : allStatusCodes) {
            Map spec = new HashMap();
            Map statusCodeMap = (Map) element;
            String statusCodeString = statusCodeMap.get(STATUS_CODE).toString();
            int statusCode = Integer.valueOf(statusCodeString);
            spec.put(STATUS_CODE, statusCode);
            Object mediatype = statusCodeMap.get(MEDIATYPE);
            if (mediatype != null) spec.put(MEDIATYPE, mediatype);
            result.add(spec);
        }
    }

}
