package com.weareadaptive.crypto.Validation;

public final class ValidationResult extends RuntimeException
{
    private static final ValidationResult SUCCESS_RESULT = new ValidationResult(ValidationResultCode.SUCCESS, "");
    private final ValidationResultCode resultCode;
    private final String errorMessage;

    public ValidationResult(final ValidationResultCode resultCode, final String errorMessage)
    {
        this.resultCode = resultCode;
        this.errorMessage = errorMessage;
    }

    public static ValidationResult success()
    {
        return SUCCESS_RESULT;
    }

    public static ValidationResult failure(final ValidationResultCode resultCode, final String errorMessage)
    {
        return new ValidationResult(resultCode, errorMessage);
    }

    public ValidationResultCode resultCode()
    {
        return resultCode;
    }

    public String errorMessage()
    {
        return errorMessage;
    }
}
