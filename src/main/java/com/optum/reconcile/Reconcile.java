package com.optum.reconcile;

import java.util.*;
import java.util.stream.Collectors;

public class Reconcile {
    public static Map<String, List<Map<String, Object>>> reconcile(
            List<Map<String, Object>> expected,
            List<Map<String, Object>> actual,
            String primaryKey) {

        Map<Object, Map<String, Object>> expectedMap = new HashMap<>();
        Map<Object, Map<String, Object>> actualMap = new HashMap<>();

        for (Map<String, Object> map : expected) {
            Object key = map.get(primaryKey);
            if (expectedMap.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate key found in expected list: " + key);
            }
            expectedMap.put(key, map);
        }

        for (Map<String, Object> map : actual) {
            Object key = map.get(primaryKey);
            if (actualMap.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate key found in actual list: " + key);
            }
            actualMap.put(key, map);
        }

        List<Map<String, Object>> missing = new ArrayList<>();
        List<Map<String, Object>> extra = new ArrayList<>();
        List<Map<String, Object>> mismatched = new ArrayList<>();

        for (Object key : expectedMap.keySet()) {
            if (!actualMap.containsKey(key)) {
                missing.add(Collections.singletonMap(primaryKey, key));
            } else {
                Map<String, Object> expectedEntry = new HashMap<>(expectedMap.get(key));
                Map<String, Object> actualEntry = actualMap.get(key);
                expectedEntry.keySet().retainAll(actualEntry.keySet()); // Ignore extra keys in expected

                List<String> mismatchedFields = new ArrayList<>();
                for (String field : expectedEntry.keySet()) {
                    if (!Objects.equals(expectedEntry.get(field), actualEntry.get(field))) {
                        mismatchedFields.add(field);
                    }
                }

                if (!mismatchedFields.isEmpty()) {
                    Map<String, Object> diff = new HashMap<>();
                    diff.put(primaryKey, key);
                    diff.put("mismatchedFields", mismatchedFields);
                    mismatched.add(diff);
                }
            }
        }

        for (Object key : actualMap.keySet()) {
            if (!expectedMap.containsKey(key)) {
                extra.add(Collections.singletonMap(primaryKey, key));
            }
        }

        Map<String, List<Map<String, Object>>> reconciliationReport = new HashMap<>();
        reconciliationReport.put("missing", missing);
        reconciliationReport.put("extra", extra);
        reconciliationReport.put("mismatched", mismatched);

        return reconciliationReport;
    }

}
