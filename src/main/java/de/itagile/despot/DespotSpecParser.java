package de.itagile.despot;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@SuppressWarnings("unchecked")
public class DespotSpecParser {

    public static final String RESPONSES = "responses";
    public static final String METHODS = "methods";
    public static final String METHOD = "method";
    public static final String MEDIATYPE = "produces";
    public static final String URI = "uri";
    public static final String ENDPOINTS = "endpoints";
    public static final String ALL_MEDIATYPES = "mediatypes";
    public static final String FIELDS = "fields";

    private static Iterator<Item> mapIterator(final Map input, final Map result) {
        final Iterator<Map.Entry> iterator = input.entrySet().iterator();
        return new Iterator<Item>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Item next() {
                Map.Entry next = iterator.next();
                Object value = next.getValue();
                String key = next.getKey().toString();
                if (value instanceof Map) {
                    return new AddMapToMapEntry(result, key, (Map) value, new HashMap());
                } else
                if (value instanceof Collection) {
                    return new AddSetToMapEntry(result, key, (Collection) value, new HashSet());
                } else {
                    return new AddValueToMapEntry(result, key, value.toString());
                }
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    private static Iterator<Item> emptyIterator() {
        return new Iterator<Item>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Item next() {
                return null;
            }

            @Override
            public void remove() {
            }
        };
    }

    private static Iterator<Item> collectionIterator(final Collection input, final Set result) {
        final Iterator iterator = input.iterator();
        return new Iterator<Item>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Item next() {
                Object value = iterator.next();
                if (value instanceof Map) {
                    return new AddMapToSetEntry(result, (Map) value, new HashMap());
                } else {
                    return new AddValueToSet(result, value.toString());
                }
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

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

    public Map<String, Object> toStringMap(Map<String, Object> input) {
        Deque<Item> commands = new ArrayDeque<>();
        Deque<Item> inputQueue = new ArrayDeque<>();
        HashMap result = new HashMap();
        RootEntry root = new RootEntry(input, result);
        inputQueue.add(root);
        while (!inputQueue.isEmpty()) {
            Item first = inputQueue.pop();
            commands.push(first);
            for (Item child : first) {
                inputQueue.add(child);
            }
        }
        while(!commands.isEmpty()) {
            commands.pop().call();
        }
        return result;
    }


    private interface Item extends Iterable<Item> {
        void call();

        @Override
        Iterator<Item> iterator();
    }

    private static class AddMapToMapEntry implements Item {

        private final Map parent;
        private final String key;
        private final Map child;
        private final Map result;

        private AddMapToMapEntry(Map parent, String key, Map child, Map result) {
            this.parent = parent;
            this.key = key;
            this.child = child;
            this.result = result;
        }

        @Override
        public void call() {
            parent.put(key, result);
        }

        @Override
        public Iterator<Item> iterator() {
            return mapIterator(child, result);
        }
    }

    private static class AddValueToMapEntry implements Item {

        private final Map parent;
        private final String key;
        private final String value;

        private AddValueToMapEntry(Map parent, String key, String value) {
            this.parent = parent;
            this.key = key;
            this.value = value;
        }

        @Override
        public void call() {
            parent.put(key, value);
        }

        @Override
        public Iterator<Item> iterator() {
            return emptyIterator();
        }
    }

    private static class AddSetToMapEntry implements Item {
        private final Map parent;
        private final String key;
        private final Collection collection;
        private final Set result;

        private AddSetToMapEntry(Map parent, String key, Collection collection, Set result) {
            this.parent = parent;
            this.key = key;
            this.collection = collection;
            this.result = result;
        }

        @Override
        public void call() {
            parent.put(key, result);
        }

        @Override
        public Iterator<Item> iterator() {
            return collectionIterator(collection, result);
        }
    }

    private static class AddValueToSet implements Item {
        private final Set parent;
        private final String value;

        public AddValueToSet(Set parent, String value) {
            this.parent = parent;
            this.value = value;
        }

        @Override
        public void call() {
            parent.add(value);
        }

        @Override
        public Iterator<Item> iterator() {
            return emptyIterator();
        }
    }

    private static class AddMapToSetEntry implements Item {
        private final Collection parent;
        private final Map child;
        private final Map result;

        private AddMapToSetEntry(Collection parent, Map child, Map result) {
            this.parent = parent;
            this.child = child;
            this.result = result;
        }

        @Override
        public void call() {
            parent.add(result);
        }

        @Override
        public Iterator<Item> iterator() {
            return mapIterator(child, result);
        }
    }

    private class RootEntry implements Item {
        private final Map map;
        private final Map result;

        public RootEntry(Map input, Map result) {
            this.map = input;
            this.result = result;
        }

        @Override
        public void call() {
        }

        @Override
        public Iterator<Item> iterator() {
            return mapIterator(map, result);
        }
    }
}
