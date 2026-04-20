package com.taha.expensetracker.model.sealed;

public sealed interface ExportRequest
        permits TransactionExportRequest, SummaryExportRequest {
}