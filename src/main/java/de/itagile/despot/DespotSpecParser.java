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
    public static final String MEDIATYPE = "produces";
    public static final String URI = "uri";
    public static final String ENDPOINTS = "endpoints";
    public static final String ALL_MEDIATYPES = "media_types";
    public static final String FIELDS = "fields";

    public Set<Map<String, Object>> getSpec(Despot.Method method, String uri, InputStream stream) throws IOException, ParseException {
        Map completeSpec = (Map) new JSONParser().parse(new InputStreamReader(stream));
        Set<Map<String, Object>> spec = extractSpecFrom(method, uri, completeSpec);
        expandMediaType(spec, completeSpec);
        return new HashSet<>(spec); // necessary, since items in spec have been modified.

    }

    private void expandMediaType(Set<Map<String, Object>> specs, Map completeSpec) {
        for (Map<String, Object> mySpec : specs) {
            Object myMediaType = mySpec.get(MEDIATYPE);
            Iterable<Map<String, Object>> allMediaTypes = (Iterable<Map<String, Object>>) completeSpec.get(ALL_MEDIATYPES);
            for (Map<String, Object> mediaType : allMediaTypes) {
                if (mediaType.get("name").equals(myMediaType)) {
                    expandFields(mediaType, completeSpec);
                    mySpec.put(MEDIATYPE, mediaType);
                }
            }
        }
    }

    private void expandFields(Map<String, Object> mediaType, Map completeSpec) {
        Set expandedFields = new HashSet<>();
        Iterable<Object> myFields = (Iterable<Object>) mediaType.get(FIELDS);
        Iterable<Map<String, Object>> fields = (Iterable<Map<String, Object>>) completeSpec.get(FIELDS);
        for (Object myField : myFields) {
            if (!(myField instanceof String)) {
                expandedFields.add(myField);
            } else {
            for (Map<String, Object> field : fields) {
                if (field.get("name").equals(myField)) {
                    expandedFields.add(new HashMap(field));
                }
            }}
        }
        mediaType.put(FIELDS, expandedFields);
    }

    public Set<Map<String, Object>> extractSpecFrom(Despot.Method method, String uri, Map completeSpec) {
        Set<Map<String, Object>> result = new HashSet<>();
        visitAllEndpoints(method, uri, completeSpec, result);
        return result;
    }

    public void visitAllEndpoints(Despot.Method method, String uri, Map completeSpec, Set<Map<String, Object>> result) {
        List endpoints = (List) completeSpec.get(ENDPOINTS);
        for (Object element : endpoints) {
            Map endpointMap = (Map) element;
            if (uri.equals(endpointMap.get(URI))) {
                visitAllMethods(method, endpointMap, result);
            }
        }
    }

    private void visitAllMethods(Despot.Method method, Map endpointMap, Set<Map<String, Object>> result) {
        List methods = (List) endpointMap.get(METHODS);
        for (Object element : methods) {
            Map methodMap = (Map) element;
            if (method.name().equals(methodMap.get(METHOD))) {
                visitAllResponses(methodMap, result);
            }
        }
    }

    private void visitAllResponses(Map methodMap, Set<Map<String, Object>> result) {
        List allStatusCodes = (List) methodMap.get(RESPONSES);
        for (Object element : allStatusCodes) {
            Map<String, Object> spec = new HashMap<>();
            Map statusCodeMap = (Map) element;
            spec.put(STATUS_CODE, statusCodeMap.get(STATUS_CODE));
            Object mediatype = statusCodeMap.get(MEDIATYPE);
            if (mediatype != null) {
                spec.put(MEDIATYPE, statusCodeMap.get(MEDIATYPE));
            }
            result.add(spec);
        }
    }

}
