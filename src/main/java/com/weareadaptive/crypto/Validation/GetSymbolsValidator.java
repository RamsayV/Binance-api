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
    private static final int MAX_SYMBOLS_PER_REQUEST = 10;

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
            ValidationResult result = validateSingleSymbol(symbol, uniqueSymbols);
            if (result.resultCode() != ValidationResultCode.SUCCESS) {
                return result;
            }
        }

        return ValidationResult.success();
    }

    private ValidationResult validateSingleSymbol(String symbol, Set<String> uniqueSymbols) {
        if (symbol == null) {
            return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL, "Symbol cannot be null");
        }

        symbol = symbol.trim().toUpperCase();

        if (symbol.isEmpty()) {
            return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL, "Symbol cannot be empty");
        }

        if (symbol.length() < MIN_SYMBOL_LENGTH || symbol.length() > MAX_SYMBOL_LENGTH) {
            return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL,
                String.format("Symbol '%s' length must be between %d and %d characters", symbol, MIN_SYMBOL_LENGTH, MAX_SYMBOL_LENGTH));
        }

        if (!VALID_SYMBOL_PATTERN.matcher(symbol).matches()) {
            return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL,
                String.format("Symbol '%s' must contain only uppercase letters and numbers", symbol));
        }

        if (RESTRICTED_SYMBOLS.contains(symbol)) {
            return ValidationResult.failure(ValidationResultCode.RESTRICTED_SYMBOL,
                String.format("Symbol '%s' is restricted and cannot be used", symbol));
        }

        if (DELISTED_SYMBOLS.contains(symbol)) {
            return ValidationResult.failure(ValidationResultCode.DELISTED_SYMBOL,
                String.format("Symbol '%s' has been delisted and is no longer available", symbol));
        }

        if (!uniqueSymbols.add(symbol)) {
            return ValidationResult.failure(ValidationResultCode.DUPLICATE_SYMBOL,
                String.format("Duplicate symbol found: %s", symbol));
        }

        if (!BinanceSymbols.contains(symbol)) {
            return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL,
                String.format("Symbol '%s' is not a valid Binance trading symbol", symbol));
        }

        return ValidationResult.success();
    }
}
