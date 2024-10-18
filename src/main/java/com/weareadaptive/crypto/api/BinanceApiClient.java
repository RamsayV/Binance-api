package com.weareadaptive.crypto.api;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class BinanceApiClient {
    private final Vertx vertx;

    public BinanceApiClient(Vertx vertx) {
        this.vertx = vertx;
    }


    //apparently web client is fully non blocking so look int0 that if you get a second  https://vertx.io/docs/vertx-web-client/java/

    public Future<JsonObject> getDataForSymbolWithLib(String symbol) {
        return vertx.executeBlocking(() -> {
            SpotClient client = new SpotClientImpl();
            Map<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("symbol", symbol);
            String result = client.createMarket().ticker(parameters);
            return new JsonObject(result);
        });
    }

    public Future<JsonArray> getDataForSymbolsWithLib(ArrayList<String> symbols) {
        return vertx.executeBlocking(() -> {
            SpotClient client = new SpotClientImpl();
            Map<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("symbols", symbols);
            String result = client.createMarket().ticker(parameters);
            return new JsonArray(result);
        });
    }
}
