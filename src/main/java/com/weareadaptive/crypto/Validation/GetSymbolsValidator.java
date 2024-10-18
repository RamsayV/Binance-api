package com.weareadaptive.crypto.Validation;

import com.weareadaptive.crypto.dto.GetSymbolsDataRequest;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class GetSymbolsValidator {
    private static final Pattern VALID_SYMBOL_PATTERN = Pattern.compile("^[A-Z0-9]{2,12}$");
    private static final int MAX_SYMBOL_LENGTH = 12;
    private static final int MIN_SYMBOL_LENGTH = 2;
    private static final Set<String> RESTRICTED_SYMBOLS = Set.of("TEST", "DEMO", "INVALID");
    private static final Set<String> DELISTED_SYMBOLS = Set.of("BCC", "BCHABC", "BCHSV"); // TODO: Update this list with more delisted symbols
    private static final int MAX_SYMBOLS_PER_REQUEST = 100; // Adjust this value based on your API limits

    public ValidationResult validate(GetSymbolsDataRequest request) {
        if (request == null || request.symbols() == null) {
            return ValidationResult.failure(ValidationResultCode.INVALID_REQUEST, "Request or symbols list cannot be null");
        }

        if (request.symbols().isEmpty()) {
            return ValidationResult.failure(ValidationResultCode.SYMBOL_DOES_NOT_EXIST, "Symbols list cannot be empty");
        }

        if (request.symbols().size() > MAX_SYMBOLS_PER_REQUEST) {
            return ValidationResult.failure(ValidationResultCode.INVALID_REQUEST,
                "Number of symbols exceeds the maximum allowed (" + MAX_SYMBOLS_PER_REQUEST + ")");
        }

        Set<String> uniqueSymbols = new HashSet<>();

        for (String symbol : request.symbols()) {
            if (symbol == null) {
                return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL, "Symbol cannot be null");
            }

            symbol = symbol.trim().toUpperCase();

            if (symbol.isEmpty()) {
                return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL, "Symbol cannot be empty");
            }

            if (symbol.length() < MIN_SYMBOL_LENGTH) {
                return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL,
                    "Symbol '" + symbol + "' length is less than the minimum allowed length of " + MIN_SYMBOL_LENGTH);
            }

            if (symbol.length() > MAX_SYMBOL_LENGTH) {
                return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL,
                    "Symbol '" + symbol + "' length exceeds maximum allowed length of " + MAX_SYMBOL_LENGTH);
            }

            if (!VALID_SYMBOL_PATTERN.matcher(symbol).matches()) {
                return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL,
                    "Symbol '" + symbol + "' must contain only uppercase letters and numbers, and be between 2 and 12 characters long");
            }

            if (RESTRICTED_SYMBOLS.contains(symbol)) {
                return ValidationResult.failure(ValidationResultCode.RESTRICTED_SYMBOL,
                    "Symbol '" + symbol + "' is restricted and cannot be used");
            }

            if (DELISTED_SYMBOLS.contains(symbol)) {
                return ValidationResult.failure(ValidationResultCode.DELISTED_SYMBOL,
                    "Symbol '" + symbol + "' has been delisted and is no longer available");
            }

            if (!uniqueSymbols.add(symbol)) {
                return ValidationResult.failure(ValidationResultCode.DUPLICATE_SYMBOL,
                    "Duplicate symbol found: " + symbol);
            }
        }

        return ValidationResult.success();
    }
}
