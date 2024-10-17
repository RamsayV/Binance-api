package com.weareadaptive.crypto.Validation;

import com.weareadaptive.crypto.dto.GetSymbolsDataRequest;

import java.util.regex.Pattern;

public class GetSymbolsValidator {
    private static final Pattern VALID_SYMBOL_PATTERN = Pattern.compile("^[A-Z0-9]{2,12}$");

    public ValidationResult validate(GetSymbolsDataRequest request) {
        if (request == null || request.symbols() == null || request.symbols().isEmpty()) {
            return ValidationResult.failure(ValidationResultCode.SYMBOL_DOES_NOT_EXIST, "Symbols list cannot be null or empty");
        }

        for (String symbol : request.symbols()) {
            if (symbol == null || symbol.isEmpty()) {
                return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL, "Symbol cannot be null or empty");
            }

            if (!VALID_SYMBOL_PATTERN.matcher(symbol).matches()) {
                return ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL,
                    "Symbol '" + symbol + "' must contain only uppercase letters and numbers, and be between 2 and 12 characters long");
            }
        }

        return ValidationResult.success();
    }
}
