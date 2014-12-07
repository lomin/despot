package de.itagile.util;

import java.util.*;

public class CollectionUtil {
    public static Map<String, Object> mapOf(Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Arguments must be pairs!");
        }
        List<Object> keyValueList = Arrays.asList(keyValuePairs);
        Iterator<Object> iterator = keyValueList.iterator();
        HashMap<String, Object> map = new HashMap<>();
        while (iterator.hasNext()) {
            map.put(iterator.next().toString(), iterator.next());
        }

        return map;
    }

    public static Set<Map<String, Object>> setOf(Map<String, Object>... maps) {
        HashSet<Map<String, Object>> result = new HashSet<>();
        Collections.addAll(result, maps);
        return result;
    }
}
