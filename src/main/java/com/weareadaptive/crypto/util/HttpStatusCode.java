package com.weareadaptive.crypto.util;

public enum HttpStatusCode
{
    OK(200),
    BAD_REQUEST(400),
    AUTHENTICATION_ERROR(401),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    private final int statusCode;

    HttpStatusCode(final int statusCode)
    {
        this.statusCode = statusCode;
    }

    public int value()
    {
        return statusCode;
    }
}
