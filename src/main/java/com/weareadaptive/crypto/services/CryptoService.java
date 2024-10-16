package com.weareadaptive.crypto.services;

import com.weareadaptive.crypto.api.BinanceApiClient;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class CryptoService {
    private static final Logger LOG = LoggerFactory.getLogger(CryptoService.class);
    private final BinanceApiClient binanceApiClient;

    public CryptoService(final Vertx vertx) {
        this.binanceApiClient = new BinanceApiClient(vertx);
    }

    public void handleGetMultipleSymbolsCryptoData(final ArrayList<String> symbols,
                                                   final Handler<JsonArray> onSuccess,
                                                   final Handler<Throwable> onFailure) {
        LOG.info("Handling Get Crypto Data request for multiple symbols: {}", symbols);

        binanceApiClient.getDataForSymbolsWithLib(symbols)
            .onSuccess(onSuccess)
            .onFailure(onFailure);
    }

    public void handleGetSingleSymbolCryptoData(final String symbol,
                                                final Handler<JsonObject> onSuccess,
                                                final Handler<Throwable> onFailure) {
        LOG.info("Handling Get Crypto Data request for single symbol: {}", symbol);

        binanceApiClient.getDataForSymbolWithLib(symbol)
            .onSuccess(onSuccess)
            .onFailure(onFailure);
    }
}
