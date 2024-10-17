package com.weareadaptive.crypto.util;

import com.weareadaptive.crypto.Validation.ValidationResult;
import com.weareadaptive.crypto.dto.GetSymbolDataResponse;
import com.weareadaptive.crypto.dto.GetSymbolsResponseData;
import com.weareadaptive.crypto.model.CryptoTickerData;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

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

    public static GetSymbolDataResponse parseSymbolData(JsonObject jsonObject)
    {
        CryptoTickerData cryptoTickerData = new CryptoTickerData(
            jsonObject.getString("symbol"),
            new BigDecimal(jsonObject.getString("lastPrice")),
            new BigDecimal(jsonObject.getString("priceChange")),
            new BigDecimal(jsonObject.getString("priceChangePercent")),
            new BigDecimal(jsonObject.getString("highPrice")),
            new BigDecimal(jsonObject.getString("lowPrice")),
            new BigDecimal(jsonObject.getString("volume")),
            Instant.ofEpochMilli(jsonObject.getLong("closeTime"))
        );
        return new GetSymbolDataResponse(cryptoTickerData);
    }

    public static <T> void parseQueryParamRequest(
        final RoutingContext context,
        final String paramName,
        final Function<List<String>, T> requestConstructor,
        final BiConsumer<RoutingContext, T> handler) {

        String param = context.request().getParam(paramName);

        List<String> paramList = Arrays.asList(param.split(","));
        T request = requestConstructor.apply(paramList);
        handler.accept(context, request);
    }

    public static GetSymbolsResponseData parseSymbolsData(JsonArray jsonArray)
    {
        ArrayList<CryptoTickerData> tickerDataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++)
        {
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            CryptoTickerData cryptoTickerData = new CryptoTickerData(
                jsonObject.getString("symbol"),
                new BigDecimal(jsonObject.getString("lastPrice")),
                new BigDecimal(jsonObject.getString("priceChange")),
                new BigDecimal(jsonObject.getString("priceChangePercent")),
                new BigDecimal(jsonObject.getString("highPrice")),
                new BigDecimal(jsonObject.getString("lowPrice")),
                new BigDecimal(jsonObject.getString("volume")),
                Instant.ofEpochMilli(jsonObject.getLong("closeTime"))
            );
            tickerDataList.add(cryptoTickerData);
        }
        return new GetSymbolsResponseData(tickerDataList);
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


    public static void handleFailure(final RoutingContext routingContext, final Throwable th)
    {
        final HttpServerResponse response = routingContext.response();
        if (th instanceof ValidationResult failure)
        {
            String errorMessage = failure.getMessage() != null ? failure.getMessage() : "An error occurred";
            switch (failure.resultCode())
            {
                case SUCCESS ->
                {
                    response.setStatusCode(200)
                        .setStatusMessage("Operation successful")
                        .end();
                }
                case INVALID_SYMBOL, SYMBOL_DOES_NOT_EXIST ->
                {
                    response.setStatusCode(400)
                        .setStatusMessage(errorMessage)
                        .end(errorMessage);
                }
                default ->
                {
                    response.setStatusCode(500)
                        .setStatusMessage("Internal Server Error")
                        .end(errorMessage);
                }
            }
        }
        else
        {
            response.setStatusCode(500)
                .setStatusMessage("An unexpected error has occurred")
                .end();
        }
    }
}

