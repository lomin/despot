package de.itagile.despot;

import java.util.*;

public class CollectionUtil {
    private CollectionUtil() {
    }

    public static Map<String, Object> mapOf(Object... keyValuePairs) {
        if (keyValuePairs == null || keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Arguments must be pairs and there must be at least one pair!");
        }
        List<Object> keyValueList = Arrays.asList(keyValuePairs);
        Iterator<Object> iterator = keyValueList.iterator();
        HashMap<String, Object> map = new HashMap<>();
        while (iterator.hasNext()) {
            map.put(iterator.next().toString(), iterator.next());
        }
        return map;
    }

    @SafeVarargs
    public static Set<Map<String, Object>> setOf(Map<String, Object>... maps) {
        Set<Map<String, Object>> result = new HashSet<>();
        Collections.addAll(result, maps);
        return result;
    }

    public static Set setOf(Object... entries) {
        Set result = new HashSet();
        Collections.addAll(result, entries);
        return result;
    }
}
