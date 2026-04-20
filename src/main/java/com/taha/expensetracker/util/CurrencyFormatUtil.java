package com.taha.expensetracker.util;

import com.taha.expensetracker.model.AppSettings;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

public final class CurrencyFormatUtil {

    private CurrencyFormatUtil() {
    }

    public static String format(BigDecimal amount, AppSettings settings) {
        Objects.requireNonNull(settings, "AppSettings must not be null.");

        BigDecimal safeAmount = amount == null ? BigDecimal.ZERO : amount;

        Locale locale = new Locale(settings.getLanguageCode(), settings.getCountryCode());
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        try {
            currencyFormatter.setCurrency(Currency.getInstance(settings.getCurrencyCode()));
        } catch (IllegalArgumentException ignored) {
            // fallback to locale default currency if saved currency code is invalid
        }

        return currencyFormatter.format(safeAmount);
    }

    public static String format(BigDecimal amount) {
        AppSettings fallback = new AppSettings("en", "IE", "EUR", "exports");
        return format(amount, fallback);
    }
}