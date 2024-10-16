package com.weareadaptive.crypto;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import com.weareadaptive.crypto.services.CryptoService;

public class CryptoWebService {
    private static final Logger LOG = LoggerFactory.getLogger(CryptoWebService.class);
    public static final String BASE_PATH = "/api/crypto/*";
    public static final String CRYPTOS_PATH = "/api/crypto";
    public static final String CRYPTO_PATH = "/api/crypto/:symbol";

    private final CryptoService cryptoService;

    public CryptoWebService(final CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public static void registerService(final Router router, final CryptoService cryptoService) {
        final CryptoWebService cryptoWebService = new CryptoWebService(cryptoService);
        router.get(CRYPTOS_PATH).handler(cryptoWebService::handleGetMultipleSymbolsCryptoData);
        router.get(CRYPTO_PATH).handler(cryptoWebService::handleGetSingleSymbolCryptoData);
    }

    public void handleGetMultipleSymbolsCryptoData(final RoutingContext context) {
        String symbolsParam = context.request().getParam("symbols");

        if (symbolsParam == null || symbolsParam.isEmpty()) {
            context.response().setStatusCode(400)
                .setStatusMessage("No symbols provided")
                .end();
            return;
        }

        ArrayList<String> symbols = new ArrayList<>(Arrays.asList(symbolsParam.split(",")));

        LOG.info("Received request for crypto data with multiple symbols: {}", symbols);

        cryptoService.handleGetMultipleSymbolsCryptoData(
            symbols,
            result -> context.response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json")
                .end(result.encode()),
            failure -> context.response()
                .setStatusCode(500)
                .end("ERROR: " + failure.getMessage())
        );
    }

    public void handleGetSingleSymbolCryptoData(final RoutingContext context) {
        String symbol = context.pathParam("symbol");

        LOG.info("Received request for crypto data with single symbol: {}", symbol);

        cryptoService.handleGetSingleSymbolCryptoData(
            symbol,
            result -> context.response()
                .putHeader("content-type", "application/json")
                .end(result.encode()),
            failure -> context.response()
                .setStatusCode(500)
                .end("Error: " + failure.getMessage())
        );
    }
}
