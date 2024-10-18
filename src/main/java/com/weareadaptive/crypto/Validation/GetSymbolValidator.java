package com.weareadaptive.crypto.Validation;
import com.weareadaptive.crypto.dto.GetSymbolDataRequest;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class GetSymbolValidator {
    private static final Pattern VALID_SYMBOL_PATTERN = Pattern.compile("^[A-Z0-9]{2,12}$");
    private static final int MAX_SYMBOL_LENGTH = 12;
    private static final int MIN_SYMBOL_LENGTH = 2;
    private static final Set<String> RESTRICTED_SYMBOLS = Set.of("TEST", "DEMO", "INVALID");
    private static final Set<String> DELISTED_SYMBOLS = Set.of("BCC", "BCHABC", "BCHSV"); // Look up delisted and add them cause I think there are mor ?


    public ValidationResult validate(GetSymbolDataRequest request) {
        if (request == null) {
            return ValidationResult.failure(ValidationResultCode.INVALID_REQUEST, "Request cannot be null");
        }

        String symbol = request.symbol();

        if (symbol == null) {
            return ValidationResult.failure(ValidationResultCode.SYMBOL_DOES_NOT_EXIST, "Symbol cannot be null");
        }

        symbol = symbol.trim();

        if (symbol.isEmpty()) {
            return ValidationResult.failure(ValidationResultCode.SYMBOL_DOES_NOT_EXIST, "Symbol cannot be empty");
        }

        if (symbol.length() < MIN_SYMBOL_LENGTH) {
            return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL,
                "Symbol length is less than the minimum allowed length of " + MIN_SYMBOL_LENGTH);
        }

        if (symbol.length() > MAX_SYMBOL_LENGTH) {
            return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL,
                "Symbol length exceeds maximum allowed length of " + MAX_SYMBOL_LENGTH);
        }

        symbol = symbol.toUpperCase();

        if (!VALID_SYMBOL_PATTERN.matcher(symbol).matches()) {
            return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL,
                "Symbol must contain only uppercase letters and numbers, and be between 2 and 12 characters long");
        }

        if (RESTRICTED_SYMBOLS.contains(symbol)) {
            return ValidationResult.failure(ValidationResultCode.RESTRICTED_SYMBOL,
                "This symbol is restricted and cannot be used");
        }

        if (DELISTED_SYMBOLS.contains(symbol)) {
            return ValidationResult.failure(ValidationResultCode.DELISTED_SYMBOL,
                "This symbol has been delisted and is no longer available");
        }

        if (!BinanceSymbols.contains(symbol)) {
            return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL,
                "Symbol '" + symbol + "' is not a valid Binance trading symbol");
        }

        return ValidationResult.success();
    }

}
