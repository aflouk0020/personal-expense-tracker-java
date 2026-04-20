package com.taha.expensetracker.model;

import java.util.Locale;
import java.util.Objects;

public final class AppSettings {

    private String languageCode;
    private String countryCode;
    private String currencyCode;
    private String exportFolder;

    public AppSettings() {
    }

    public AppSettings(String languageCode, String countryCode, String currencyCode, String exportFolder) {
        setLanguageCode(languageCode);
        setCountryCode(countryCode);
        setCurrencyCode(currencyCode);
        setExportFolder(exportFolder);
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = Objects.requireNonNull(languageCode).trim();
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = Objects.requireNonNull(countryCode).trim();
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = Objects.requireNonNull(currencyCode).trim();
    }

    public String getExportFolder() {
        return exportFolder;
    }

    public void setExportFolder(String exportFolder) {
        this.exportFolder = Objects.requireNonNull(exportFolder).trim();
    }

    public Locale toLocale() {
        return new Locale(languageCode, countryCode);
    }

    @Override
    public String toString() {
        return "AppSettings{" +
                "languageCode='" + languageCode + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", exportFolder='" + exportFolder + '\'' +
                '}';
    }
}