package com.weareadaptive.crypto.Validation;

import com.weareadaptive.crypto.dto.GetSymbolDataRequest;

import java.util.Set;
import java.util.regex.Pattern;

public class GetSymbolValidator {
    private static final Pattern VALID_SYMBOL_PATTERN = Pattern.compile("^[A-Z0-9]{2,12}$");
    private static final int MAX_SYMBOL_LENGTH = 12;
    private static final int MIN_SYMBOL_LENGTH = 2;
    private static final Set<String> RESTRICTED_SYMBOLS = Set.of("TEST", "DEMO", "INVALID");
    private static final Set<String> DELISTED_SYMBOLS = Set.of("BCC", "BCHABC", "BCHSV"); // try and find more delisted symbolds

    public ValidationResult validate(GetSymbolDataRequest request) {
        if (request == null) {
            return createFailureResult(ValidationResultCode.INVALID_REQUEST, "Request cannot be null");
        }

        String symbol = request.symbol();

        ValidationResult nullOrEmptyCheck = checkNullOrEmpty(symbol);
        if (nullOrEmptyCheck != null) return nullOrEmptyCheck;

        symbol = symbol.trim().toUpperCase();

        ValidationResult lengthCheck = checkLength(symbol);
        if (lengthCheck != null) return lengthCheck;

        ValidationResult patternCheck = checkPattern(symbol);
        if (patternCheck != null) return patternCheck;

        ValidationResult restrictionCheck = checkRestrictions(symbol);
        if (restrictionCheck != null) return restrictionCheck;

        ValidationResult binanceCheck = checkBinanceValidity(symbol);
        if (binanceCheck != null) return binanceCheck;

        return ValidationResult.success();
    }

    private ValidationResult checkNullOrEmpty(String symbol) {
        if (symbol == null) {
            return createFailureResult(ValidationResultCode.SYMBOL_DOES_NOT_EXIST, "Symbol cannot be null");
        }
        if (symbol.trim().isEmpty()) {
            return createFailureResult(ValidationResultCode.SYMBOL_DOES_NOT_EXIST, "Symbol cannot be empty");
        }
        return null;
    }

    private ValidationResult checkLength(String symbol) {
        if (symbol.length() < MIN_SYMBOL_LENGTH) {
            return createFailureResult(ValidationResultCode.INVALID_SYMBOL,
                String.format("Symbol length is less than the minimum allowed length of %d", MIN_SYMBOL_LENGTH));
        }
        if (symbol.length() > MAX_SYMBOL_LENGTH) {
            return createFailureResult(ValidationResultCode.INVALID_SYMBOL,
                String.format("Symbol length exceeds maximum allowed length of %d", MAX_SYMBOL_LENGTH));
        }
        return null;
    }

    private ValidationResult checkPattern(String symbol) {
        if (!VALID_SYMBOL_PATTERN.matcher(symbol).matches()) {
            return createFailureResult(ValidationResultCode.INVALID_SYMBOL,
                "Symbol must contain only uppercase letters and numbers, and be between 2 and 12 characters long");
        }
        return null;
    }

    private ValidationResult checkRestrictions(String symbol) {
        if (RESTRICTED_SYMBOLS.contains(symbol)) {
            return createFailureResult(ValidationResultCode.RESTRICTED_SYMBOL,
                "This symbol is restricted and cannot be used");
        }
        if (DELISTED_SYMBOLS.contains(symbol)) {
            return createFailureResult(ValidationResultCode.DELISTED_SYMBOL,
                "This symbol has been delisted and is no longer available");
        }
        return null;
    }

    private ValidationResult checkBinanceValidity(String symbol) {
        if (!BinanceSymbols.contains(symbol)) {
            return createFailureResult(ValidationResultCode.INVALID_SYMBOL,
                String.format("Symbol '%s' is not a valid Binance trading symbol", symbol));
        }
        return null;
    }

    private ValidationResult createFailureResult(ValidationResultCode code, String message) {
        return ValidationResult.failure(code, message);
    }
}
