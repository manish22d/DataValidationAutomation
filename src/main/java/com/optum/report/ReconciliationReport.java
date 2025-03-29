package com.optum.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
}
