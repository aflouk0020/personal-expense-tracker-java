package com.taha.expensetracker.model.sealed;

public final class TransactionExportRequest implements ExportRequest {

    private final String exportFolder;

    public TransactionExportRequest(String exportFolder) {
        this.exportFolder = exportFolder;
    }

    public String getExportFolder() {
        return exportFolder;
    }
}