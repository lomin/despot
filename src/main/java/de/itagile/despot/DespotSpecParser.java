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
    public static final String MEDIATYPE = "produces";
    public static final String URI = "uri";
    public static final String ENDPOINTS = "endpoints";
    public static final String ALL_MEDIATYPES = "mediatypes";
    public static final String FIELDS = "fields";

    public Set<Map<String, Object>> getSpec(Method method, String uri, InputStream stream) throws IOException, ParseException {
        Map completeSpec = (Map) new JSONParser().parse(new InputStreamReader(stream));
        List<Map<String, Object>> spec = extractSpecFrom(method, uri, completeSpec);
        expandMediaType(spec, completeSpec);
        addAdditionalSpecification(method, uri, spec, completeSpec);
        removeDescription(spec);
        return new HashSet<>(spec); // necessary, since items in spec have been modified.

    }

    private void addAdditionalSpecification(Method method, String uri, List<Map<String, Object>> spec, Map completeSpec) {
        List endpoints = (List) completeSpec.get(ENDPOINTS);
        for (Object element : endpoints) {
            Map endpointMap = (Map) element;
            if (uri.equals(endpointMap.get(URI))) {
                List methods = (List) endpointMap.get(METHODS);
                for (Object element1 : methods) {
                    Map<String, Object> methodMap = (Map<String, Object>) element1;
                    if (method.name().equals(methodMap.get(METHOD))) {
                        for (String key : methodMap.keySet()) {
                            if (!key.equals(RESPONSES)) {
                                for (Map<String, Object> stringObjectMap : spec) {
                                    stringObjectMap.put(key, methodMap.get(key));
                                }
                            }
                        }
                    }
                }
            }
        }
        Iterator<Map<String, Object>> iterator = spec.iterator();
    }

    private void removeDescription(List<Map<String, Object>> spec) {
        for (Map<String, Object> map : spec) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (next.equals("__description__")) {
                    iterator.remove();
                }
            }
        }
    }

    private void expandMediaType(List<Map<String, Object>> specs, Map completeSpec) {
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
                }
            }
        }
        mediaType.put(FIELDS, expandedFields);
    }

    public List<Map<String, Object>> extractSpecFrom(Method method, String uri, Map completeSpec) {
        List<Map<String, Object>> result = new ArrayList<>();
        visitAllEndpoints(method, uri, completeSpec, result);
        return result;
    }

    public void visitAllEndpoints(Method method, String uri, Map completeSpec, List<Map<String, Object>> result) {
        List endpoints = (List) completeSpec.get(ENDPOINTS);
        for (Object element : endpoints) {
            Map endpointMap = (Map) element;
            if (uri.equals(endpointMap.get(URI))) {
                visitAllMethods(method, endpointMap, result);
            }
        }
    }

    private void visitAllMethods(Method method, Map endpointMap, List<Map<String, Object>> result) {
        List methods = (List) endpointMap.get(METHODS);
        for (Object element : methods) {
            Map methodMap = (Map) element;
            if (method.name().equals(methodMap.get(METHOD))) {
                List allResponses = (List) methodMap.get(RESPONSES);
                result.addAll(allResponses);
            }
        }
    }

}
