package de.itagile.despot;

import de.itagile.despot.http.ConsumesSpecified;
import de.itagile.mediatype.MediaType;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static de.itagile.mediatype.MediaType.findMediaTypeByName;
import static java.util.Collections.emptyIterator;

@SuppressWarnings("unchecked")
public class DespotSpecParser {

    public static final String RESPONSES = "responses";
    public static final String METHODS = "methods";
    public static final String METHOD = "method";
    public static final String URI = "uri";
    public static final String ENDPOINTS = "endpoints";

    private static Iterator<Node> mapIterator(final Map input, final Map result, final NodeFactoryMap extensionFields) {
        final Iterator<Map.Entry> iterator = input.entrySet().iterator();
        return new Iterator<Node>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Node next() {
                Map.Entry next = iterator.next();
                Object value = next.getValue();
                String key = next.getKey().toString();
                if (extensionFields.contains(key)) {
                    return extensionFields.create(key, result, value, extensionFields);
                } else if (value instanceof Map) {
                    return new AddMapToMapEntry(result, key, (Map) value, new HashMap(), extensionFields);
                } else if (value instanceof Collection) {
                    return new AddSetToMapEntry(result, key, (Collection) value, extensionFields);
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

    private static Iterator<Node> collectionIterator(final Collection input, final Set result, final NodeFactoryMap extensionFields) {
        final Iterator iterator = input.iterator();
        return new Iterator<Node>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Node next() {
                Object value = iterator.next();
                if (value instanceof Map) {
                    return new AddMapToSetEntry(result, (Map) value, new HashMap(), extensionFields);
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

    public Set<Map<String, Object>> getSpec(Method method, String uri, ConsumesSpecified.Consumes consumes, InputStream stream) throws IOException, ParseException {
        Map completeSpec = (Map) new JSONParser().parse(new InputStreamReader(stream));
        return getSpec(method, uri, consumes, completeSpec, completeSpec);
    }

    public Set<Map<String, Object>> getSpec(Method method, String uri, ConsumesSpecified.Consumes consumes, InputStream specStream, InputStream mediaTypeSpecStream) throws IOException, ParseException {
        Map completeSpec = (Map) new JSONParser().parse(new InputStreamReader(specStream));
        Map mediaTypeSpec = (Map) new JSONParser().parse(new InputStreamReader(mediaTypeSpecStream));
        return getSpec(method, uri, consumes, completeSpec, mediaTypeSpec);
    }

    private Set<Map<String, Object>> getSpec(Method method, String uri, ConsumesSpecified.Consumes consumes, Map completeSpec, Map mediaTypeSpec) {
        completeSpec = normalize(completeSpec);
        mediaTypeSpec = normalize(mediaTypeSpec);
        Set result = expand(completeSpec, mediaTypeSpec);
        Set<Transformation> transformations = new HashSet<>();
        transformations.add(ConsumesSpecified.createTransformation());
        return filter(transform(result, transformations), method, uri, consumes, mediaTypeSpec);
    }

    private Set<Map<String, Object>> transform(Set<Map<String, Object>> input, Set<Transformation> transformations) {
        Set<Map<String, Object>> result = new HashSet<>();
        for (Map<String, Object> spec : input) {
            for (Transformation transformation : transformations) {
                spec = transformation.transform(spec);
            }
            result.add(spec);
        }
        return result;
    }

    private Set<Map<String, Object>> filter(Set<Map<String, Object>> input, Method method, String uri, ConsumesSpecified.Consumes consumes, Map mediaTypeSpec) {
        Set<Map<String, Object>> result = new HashSet<>(input);
        Iterator<Map<String, Object>> iterator = result.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> spec = iterator.next();
            if (!method.name().equals(spec.get(METHOD))) {
                iterator.remove();
            } else if (!uri.equals(spec.get(URI))) {
                iterator.remove();
            } else if (!matchesMediaTypeNames(consumes, spec.get(ConsumesSpecified.KEY))
                    && !matchesCompleteMediaTypes(consumes, mediaTypeSpec, spec.get(ConsumesSpecified.KEY))) {
                iterator.remove();
            }
        }
        return result;
    }

    private boolean matchesCompleteMediaTypes(ConsumesSpecified.Consumes consumes, Map mediaTypeSpec, Object actualConsumesSpec) {
        return actualConsumesSpec.equals(findMediaTypeByName(consumes.name(), mediaTypeSpec));
    }

    private boolean matchesMediaTypeNames(ConsumesSpecified.Consumes consumes, Object actualConsumesSpec) {
        return consumes.name().equals(actualConsumesSpec);
    }

    public Map<String, Object> normalize(Map<String, Object> input) {
        RootEntry root = new RootEntry(input);
        walk(root);
        return root.result();
    }

    private void walk(Node root) {
        Deque<Node> commands = new ArrayDeque<>();
        Deque<Node> inputQueue = new ArrayDeque<>();
        inputQueue.add(root);
        while (!inputQueue.isEmpty()) {
            Node first = inputQueue.pop();
            commands.push(first);
            for (Node child : first) {
                inputQueue.add(child);
            }
        }
        while (!commands.isEmpty()) {
            commands.pop().call();
        }
    }

    public Set expand(Map input, Map mediaTypeSpec) {
        NodeFactoryMap extensionFields = new NodeFactoryMap();
        extensionFields.put(ENDPOINTS, ExpandNode.create(ENDPOINTS, METHODS));
        extensionFields.put(METHODS, ExpandNode.create(METHODS, RESPONSES));
        extensionFields.put(ConsumesSpecified.KEY, ConsumesSpecified.createNode(mediaTypeSpec));
        extensionFields.put(MediaType.KEY, MediaType.createNode(mediaTypeSpec));
        ExpandRoot root = new ExpandRoot(input, extensionFields);
        walk(root);
        return root.result();
    }

    public interface NodeFactory {
        Node create(Map<String, Object> result, Object value, NodeFactoryMap extensionFields);
    }

    public interface Node extends Iterable<Node> {
        void call();

        @Override
        Iterator<Node> iterator();
    }

    public interface Transformation {
        Map<String, Object> transform(Map<String, Object> spec);
    }

    public static class NodeFactoryMap {

        private Map<String, NodeFactory> map = new HashMap<>();

        public boolean contains(String key) {
            return map.containsKey(key);
        }

        public Node create(String key, Map result, Object value, NodeFactoryMap extensionFields) {
            return map.get(key).create(result, value, extensionFields);
        }

        public void put(String key, NodeFactory nodeFactory) {
            map.put(key, nodeFactory);
        }
    }

    private static class AddMapToMapEntry implements Node {

        private final Map parent;
        private final String key;
        private final Map child;
        private final Map result;
        private final NodeFactoryMap extensionFields;

        private AddMapToMapEntry(Map parent, String key, Map child, Map result, NodeFactoryMap extensionFields) {
            this.parent = parent;
            this.key = key;
            this.child = child;
            this.result = result;
            this.extensionFields = extensionFields;
        }

        @Override
        public void call() {
            parent.put(key, result);
        }

        @Override
        public Iterator<Node> iterator() {
            return mapIterator(child, result, extensionFields);
        }
    }

    private static class AddValueToMapEntry implements Node {

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
            if (!"__description__".equals(key)) {
                parent.put(key, value);
            }
        }

        @Override
        public Iterator<Node> iterator() {
            return emptyIterator();
        }
    }

    private static class AddSetToMapEntry implements Node {
        private final Map parent;
        private final String key;
        private final Collection collection;
        private final Set result;
        private final NodeFactoryMap extensionFields;

        private AddSetToMapEntry(Map parent, String key, Collection collection, NodeFactoryMap extensionFields) {
            this.parent = parent;
            this.key = key;
            this.collection = collection;
            this.extensionFields = extensionFields;
            this.result = new HashSet();
        }

        @Override
        public void call() {
            parent.put(key, result);
        }

        @Override
        public Iterator<Node> iterator() {
            return collectionIterator(collection, result, extensionFields);
        }
    }

    private static class AddValueToSet implements Node {
        private final Set parent;
        private final String value;

        private AddValueToSet(Set parent, String value) {
            this.parent = parent;
            this.value = value;
        }

        @Override
        public void call() {
            parent.add(value);
        }

        @Override
        public Iterator<Node> iterator() {
            return emptyIterator();
        }
    }

    private static class AddMapToSetEntry implements Node {
        private final Collection parent;
        private final Map child;
        private final Map result;
        private final NodeFactoryMap extensionFields;

        private AddMapToSetEntry(Collection parent, Map child, Map result, NodeFactoryMap extensionFields) {
            this.parent = parent;
            this.child = child;
            this.result = result;
            this.extensionFields = extensionFields;
        }

        @Override
        public void call() {
            parent.add(result);
        }

        @Override
        public Iterator<Node> iterator() {
            return mapIterator(child, result, extensionFields);
        }
    }

    private static class ExpandNode extends AddSetToMapEntry {

        private final String expandTarget;

        private ExpandNode(Map parent, String key, Collection collection, NodeFactoryMap extensionFields, String expandTarget) {
            super(parent, key, collection, extensionFields);
            this.expandTarget = expandTarget;
        }

        public static NodeFactory create(final String key, final String expandTarget) {
            return new NodeFactory() {
                @Override
                public Node create(Map result, Object value, NodeFactoryMap extensionFields) {
                    return new ExpandNode(result, key, (Collection) value, extensionFields, expandTarget);
                }
            };
        }

        @Override
        public void call() {
            Set<Map> result = new HashSet();
            for (Object map : super.result) {
                Map<String, Object> newItem = new HashMap<>((Map) map);
                Set<Map> responses = (Set) newItem.get(expandTarget);
                newItem.remove(expandTarget);
                for (Map response : responses) {
                    response.putAll(newItem);
                }
                result.addAll(responses);
            }
            super.parent.put(super.key, result);
        }
    }

    private static class ExpandRoot implements Node {

        private final Map input;
        private final NodeFactoryMap extensionFields;
        private final HashMap result;

        public ExpandRoot(Map input, NodeFactoryMap extensionFields) {
            this.input = input;
            this.extensionFields = extensionFields;
            this.result = new HashMap();
        }

        @Override
        public void call() {
        }

        @Override
        public Iterator<Node> iterator() {
            return mapIterator(input, result, extensionFields);
        }

        public Set result() {
            return (Set) result.get("endpoints");
        }
    }

    private class RootEntry implements Node {
        private final Map map;
        private final Map result;
        private final NodeFactoryMap extensionFields;

        public RootEntry(Map input) {
            this.map = input;
            this.result = new HashMap();
            this.extensionFields = new NodeFactoryMap();
        }

        @Override
        public void call() {
        }

        @Override
        public Iterator<Node> iterator() {
            return mapIterator(map, result, extensionFields);
        }

        public Map<String, Object> result() {
            return result;
        }
    }
}
