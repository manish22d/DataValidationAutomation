package com.optum.reconcile;

import java.util.*;
import java.util.stream.Collectors;

public class Reconcile {
    public static Map<String, List<Map<String, Object>>> reconcileLists(
            List<Map<String, Object>> list1,
            List<Map<String, Object>> list2,
            String uniqueKey) {

        Map<Object, Map<String, Object>> map1 = list1.stream()
                .collect(Collectors.toMap(m -> m.get(uniqueKey), m -> m));

        Map<Object, Map<String, Object>> map2 = list2.stream()
                .collect(Collectors.toMap(m -> m.get(uniqueKey), m -> m));

        List<Map<String, Object>> matched = new ArrayList<>();
        List<Map<String, Object>> mismatched = new ArrayList<>();
        List<Map<String, Object>> missing = new ArrayList<>();

        Set<Object> allKeys = new HashSet<>();
        allKeys.addAll(map1.keySet());
        allKeys.addAll(map2.keySet());

        for (Object key : allKeys) {
            Map<String, Object> entry1 = map1.get(key);
            Map<String, Object> entry2 = map2.get(key);

            if (entry1 != null && entry2 != null) {
                if (entry1.equals(entry2)) {
                    matched.add(entry1);
                } else {
                    mismatched.add(entry1);
                    mismatched.add(entry2);
                }
            } else {
                missing.add(entry1 != null ? entry1 : entry2);
            }
        }

        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("matched", matched);
        result.put("mismatched", mismatched);
        result.put("missing", missing);
        return result;
    }
}
