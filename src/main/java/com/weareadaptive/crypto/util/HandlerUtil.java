package com.weareadaptive.crypto.util;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.function.BiConsumer;

public class HandlerUtil
{


    public static <T> void parseRequest(final RoutingContext routingContext,
                                        final Class<T> expectedType,
                                        final BiConsumer<RoutingContext, T> onSuccess,
                                        final String... pathParameters)
    {
        try
        {
            final JsonObject jsonObject = routingContext.body().isEmpty() ? new JsonObject() : routingContext.body().asJsonObject();
            parseRequest(routingContext, jsonObject, expectedType, onSuccess, pathParameters);
        }
        catch (final Exception exception)
        {
            routingContext.fail(HttpStatusCode.BAD_REQUEST.value(), exception);
        }
    }

    public static <T> void parseRequest(final RoutingContext routingContext,
                                        final JsonObject jsonObject,
                                        final Class<T> expectedType,
                                        final BiConsumer<RoutingContext, T> onSuccess,
                                        final String... pathParameters)
    {
        for (final String pathParam : pathParameters)
        {
            jsonObject.put(pathParam, routingContext.pathParam(pathParam));
        }

        final T object = jsonObject.mapTo(expectedType);
        onSuccess.accept(routingContext, object);
    }
}

//    public static void handleFailure(final RoutingContext routingContext, final Throwable th) {
//        final HttpServerResponse response = routingContext.response();
//        if (th instanceof ValidationResult failure) {
//            switch (failure.resultCode()) {
//                case SUCCESS -> {
//                    response.setStatusCode(HttpStatusCode.OK.value())
//                        .setStatusMessage("Operation successful")
//                        .end();
//                }
//
//}

