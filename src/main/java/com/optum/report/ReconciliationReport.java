package com.optum.report;

import io.cucumber.datatable.DataTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReconciliationReport {
    public List<Map<String, Object>> missing;
    public List<Map<String, Object>> extra;
    public List<Map<String, Object>> mismatched;

    public ReconciliationReport() {
        this.missing = new ArrayList<>();
        this.extra = new ArrayList<>();
        this.mismatched = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Missing: " + missing + "\nExtra: " + extra + "\nMismatched: " + mismatched;
    }


    public void generateReportTable() {
        List<List<String>> table = new ArrayList<>();

        // Add header row
        table.add(List.of("Type", "Key", "Expected Value", "Actual Value"));

        // Add Missing entries
        for (Map<String, Object> entry : missing) {
            String key = Objects.toString(entry.get("name"), "UNKNOWN");  // ✅ Handle null
            table.add(List.of("❌ Missing", key, entry.toString(), "N/A"));
        }

        // Add Extra entries
        for (Map<String, Object> entry : extra) {
            String key = Objects.toString(entry.get("name"), "UNKNOWN");
            table.add(List.of("✅ Extra", key, "N/A", entry.toString()));
        }

        // Add Mismatched entries
        for (Map<String, Object> entry : mismatched) {
            String key = Objects.toString(entry.get("name"), "UNKNOWN");
            table.add(List.of("⚠️ Mismatch", key, entry.toString(), "DIFFERENT"));
        }
        DataTable dataTable = DataTable.create(table);
        System.out.println(dataTable);

    }
}
