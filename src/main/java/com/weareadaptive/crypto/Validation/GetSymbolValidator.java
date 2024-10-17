package com.weareadaptive.crypto.Validation;
import com.weareadaptive.crypto.dto.GetSymbolDataRequest;

import java.util.regex.Pattern;

public class GetSymbolValidator {
    private static final Pattern VALID_SYMBOL_PATTERN = Pattern.compile("^[A-Z0-9]{2,12}$");


    public ValidationResult validate(GetSymbolDataRequest request) {
        if (request == null || request.symbol().isEmpty()) {
            return ValidationResult.failure(ValidationResultCode.SYMBOL_DOES_NOT_EXIST, "Symbol cannot be null or empty");
        }

        if (!VALID_SYMBOL_PATTERN.matcher(request.symbol()).matches()) {
            return  ValidationResult.failure(ValidationResultCode.INVALID_SYMBOL, "Symbol must contain only uppercase letters and numbers, and be between 2 and 12 characters long");
        }

        return ValidationResult.success();
    }

}
