package com.taha.expensetracker.service;

import com.taha.expensetracker.model.AppSettings;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

public class LocalizationService {

    private static final String BUNDLE_BASE_NAME = "messages";

    private Locale currentLocale;
    private ResourceBundle resourceBundle;

    public LocalizationService() {
        this(Locale.ENGLISH);
    }

    public LocalizationService(Locale locale) {
        setLocale(locale);
    }

    public LocalizationService(AppSettings settings) {
        this(Objects.requireNonNull(settings, "AppSettings must not be null.").toLocale());
    }

    public void setLocale(Locale locale) {
        this.currentLocale = Objects.requireNonNull(locale, "Locale must not be null.");
        this.resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, currentLocale);
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public String get(String key) {
        Objects.requireNonNull(key, "Resource key must not be null.");

        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    public String getOrDefault(String key, String defaultValue) {
        Objects.requireNonNull(defaultValue, "Default value must not be null.");

        try {
            return get(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean isEnglish() {
        return Locale.ENGLISH.getLanguage().equalsIgnoreCase(currentLocale.getLanguage());
    }

    public boolean isFrench() {
        return Locale.FRENCH.getLanguage().equalsIgnoreCase(currentLocale.getLanguage());
    }
}