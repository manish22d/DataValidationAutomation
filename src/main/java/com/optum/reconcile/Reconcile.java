package com.optum.reconcile;

import com.optum.report.ReconciliationReport;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class Reconcile {

    public ReconciliationReport reconcile(List<Map<String, Object>> expected, List<Map<String, Object>> actual) {
        return reconcile(expected, actual, null); // Delegate to the other method
    }

    public ReconciliationReport reconcile(List<Map<String, Object>> expected, List<Map<String, Object>> actual, String primaryKey) {
        ReconciliationReport report = new ReconciliationReport();

        // Convert lists to maps for efficient lookup
        Map<String, Map<String, Object>> expectedMap = convertListToMap(expected, primaryKey);
        Map<String, Map<String, Object>> actualMap = convertListToMap(actual, primaryKey);

        // Merge keys to find all unique entries
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(expectedMap.keySet());
        allKeys.addAll(actualMap.keySet());

        for (String key : allKeys) {
            Map<String, Object> expectedValue = expectedMap.get(key);
            Map<String, Object> actualValue = actualMap.get(key);

            if (expectedValue == null) {
                report.extra.add(actualValue); // Extra in actual
            } else if (actualValue == null) {
                report.missing.add(expectedValue); // Missing in actual
            } else if (!expectedValue.equals(actualValue)) {
                report.mismatched.add(expectedValue); // Exists but values differ
            }
        }

        return report;
    }

    private Map<String, Map<String, Object>> convertListToMap(List<Map<String, Object>> list, String primaryKey) {
        return list.stream()
                .collect(Collectors.toMap(
                        item -> primaryKey != null && item.containsKey(primaryKey) ? item.get(primaryKey).toString() : serializeMap(item),
                        item -> item,
                        (existing, replacement) -> existing // Handle duplicates by keeping first entry
                ));
    }

    private String serializeMap(Map<String, Object> map) {
        return new TreeMap<>(map).toString(); // Ensures consistent key order
    }


}
