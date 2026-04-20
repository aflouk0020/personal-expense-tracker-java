package com.taha.expensetracker.model.sealed;

public final class SummaryExportRequest implements ExportRequest {

    private final String exportFolder;

    public SummaryExportRequest(String exportFolder) {
        this.exportFolder = exportFolder;
    }

    public String getExportFolder() {
        return exportFolder;
    }
}