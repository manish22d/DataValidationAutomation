package com.optum.reconcile;

import com.optum.report.ReconciliationReport;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class Reconcile {

    public ReconciliationReport reconcile(
            List<Map<String, Object>> expected,
            List<Map<String, Object>> actual,
            String primaryKey) {

        List<Map<String, Object>> missing = new ArrayList<>();
        List<Map<String, Object>> extra = new ArrayList<>();
        List<Map<String, Object>> mismatched = new ArrayList<>();

        if (primaryKey == null || primaryKey.isEmpty()) {
            // If no primary key is provided, perform reconciliation based on content match
            Set<Map<String, Object>> expectedSet = new HashSet<>(expected);
            Set<Map<String, Object>> actualSet = new HashSet<>(actual);

            missing.addAll(expectedSet.stream().filter(e -> !actualSet.contains(e)).toList());
            extra.addAll(actualSet.stream().filter(a -> !expectedSet.contains(a)).toList());
        } else {
            // If primary key is available, reconcile based on it
            Map<Object, Map<String, Object>> expectedMap = new HashMap<>();
            Map<Object, Map<String, Object>> actualMap = new HashMap<>();

            for (Map<String, Object> map : expected) {
                Object key = map.get(primaryKey);
                if (key == null) {
                    continue; // Skip records without primary key
                }
                expectedMap.put(key, map);
            }

            for (Map<String, Object> map : actual) {
                Object key = map.get(primaryKey);
                if (key == null) {
                    continue; // Skip records without primary key
                }
                actualMap.put(key, map);
            }

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
        }

        return new ReconciliationReport(missing, extra, mismatched);
    }


    public ReconciliationReport reconcile(List<Map<String, Object>> outputFromExcel, List<Map<String, Object>> outputFromDb) {
        return reconcile(outputFromExcel, outputFromDb, null);
    }
}
