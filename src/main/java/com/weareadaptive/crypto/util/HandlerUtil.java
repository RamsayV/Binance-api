package com.weareadaptive.crypto.util;

import com.weareadaptive.crypto.Validation.ValidationResult;
import com.weareadaptive.crypto.dto.GetSymbolDataResponse;
import com.weareadaptive.crypto.dto.GetSymbolsResponseData;
import com.weareadaptive.crypto.model.CryptoTickerData;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class HandlerUtil {

    public static <T> Future<T> parseRequest(final RoutingContext routingContext,
                                             final Class<T> expectedType,
                                             final String... pathParameters) {
        return Future.future(promise -> {
            try {
                final JsonObject jsonObject = routingContext.body().isEmpty() ? new JsonObject() : routingContext.body().asJsonObject();
                for (final String pathParam : pathParameters) {
                    jsonObject.put(pathParam, routingContext.pathParam(pathParam));
                }
                final T object = jsonObject.mapTo(expectedType);
                promise.complete(object);
            } catch (final Exception exception) {
                promise.fail(new HttpStatusException(HttpStatusCode.BAD_REQUEST.value(), exception.getMessage()));
            }
        });
    }


    //These might actually be better placed in a helper folder but they are also parsing data so keep em here for the time being
    // Not worth turning that crypto tickerdata parser into a function when using twice
    public static GetSymbolDataResponse parseSymbolData(JsonObject jsonObject) {
        CryptoTickerData cryptoTickerData = new CryptoTickerData(
            jsonObject.getString("symbol"),
            new BigDecimal(jsonObject.getString("lastPrice")),
            new BigDecimal(jsonObject.getString("openPrice")),
            new BigDecimal(jsonObject.getString("priceChange")),
            new BigDecimal(jsonObject.getString("priceChangePercent")),
            new BigDecimal(jsonObject.getString("highPrice")),
            new BigDecimal(jsonObject.getString("lowPrice")),
            new BigDecimal(jsonObject.getString("volume")),
            Instant.ofEpochMilli(jsonObject.getLong("closeTime"))
        );
        return new GetSymbolDataResponse(cryptoTickerData);
    }

    public static <T> Future<T> parseQueryParamRequest(final RoutingContext context,
                                                       final String paramName,
                                                       final Function<List<String>, T> requestConstructor) {
        return Future.future(promise -> {
            try {
                String param = context.request().getParam(paramName);
                if (param == null || param.isEmpty()) {
                    promise.fail(new HttpStatusException(HttpStatusCode.BAD_REQUEST.value(), "Missing required query parameter: " + paramName));
                    return;
                }  // this could probably be handled elsewhere
                List<String> paramList = Arrays.asList(param.split(","));
                T request = requestConstructor.apply(paramList);
                promise.complete(request);
            } catch (Exception e) {
                promise.fail(new HttpStatusException(HttpStatusCode.BAD_REQUEST.value(), e.getMessage()));
            }
        });
    }

    public static GetSymbolsResponseData parseSymbolsData(JsonArray jsonArray) {
        ArrayList<CryptoTickerData> tickerDataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            CryptoTickerData cryptoTickerData = new CryptoTickerData(
                jsonObject.getString("symbol"),
                new BigDecimal(jsonObject.getString("lastPrice")),
                new BigDecimal(jsonObject.getString("openPrice")),
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

    public static void handleFailure(final RoutingContext routingContext, final Throwable th) {
        final HttpServerResponse response = routingContext.response();
        JsonObject errorResponse = new JsonObject();

        if (th instanceof ValidationResult failure) {
            String errorMessage = failure.getMessage() != null ? failure.getMessage() : "An error occurred and you need to Log to find out cause it hasn't been accounted for";
            int statusCode;
            switch (failure.resultCode()) {
                case SYMBOL_DOES_NOT_EXIST,
                     INVALID_SYMBOL,
                     INVALID_REQUEST,
                     RESTRICTED_SYMBOL,
                     SYMBOL_NOT_SUPPORTED,
                     DELISTED_SYMBOL,
                     SYMBOL_CASE_MISMATCH,
                     DUPLICATE_SYMBOL -> statusCode = 400;
                default -> statusCode = 500;
            }  // creating an error message in json object is a nice way of doing it imo
            errorResponse.put("error", errorMessage)
                .put("code", failure.resultCode().toString())
                .put("status", statusCode);
        } else if (th instanceof HttpStatusException httpEx) {
            errorResponse.put("error", httpEx.getMessage())
                .put("code", "HTTP_ERROR")
                .put("status", httpEx.getStatusCode());
        } else {
            errorResponse.put("error", "An unexpected error has occurred")
                .put("code", "INTERNAL_ERROR")
                .put("status", 500);
        }

        response.setStatusCode(errorResponse.getInteger("status"))
            .putHeader("Content-Type", "application/json")
            .end(errorResponse.encode());
    }
}
