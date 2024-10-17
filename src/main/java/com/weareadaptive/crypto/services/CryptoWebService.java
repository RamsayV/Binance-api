package com.weareadaptive.crypto;

import com.weareadaptive.crypto.Validation.GetSymbolValidator;
import com.weareadaptive.crypto.dto.GetSymbolDataRequest;
import com.weareadaptive.crypto.dto.GetSymbolsDataRequest;
import com.weareadaptive.crypto.util.HandlerUtil;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import com.weareadaptive.crypto.services.CryptoService;

public class CryptoWebService
{
    private static final Logger LOG = LoggerFactory.getLogger(CryptoWebService.class);
    public static final String BASE_PATH = "/api/crypto/*";
    public static final String CRYPTOS_PATH = "/api/crypto";
    public static final String CRYPTO_PATH = "/api/crypto/:symbol";

    private final CryptoService cryptoService;

    public CryptoWebService(final CryptoService cryptoService)
    {
        this.cryptoService = cryptoService;
    }

    public static void registerService(final Router router, final CryptoService cryptoService)
    {
        final CryptoWebService cryptoWebService = new CryptoWebService(cryptoService);
        router.get(CRYPTOS_PATH).handler(cryptoWebService::handleGetMultipleSymbolsCryptoData);
        router.get(CRYPTO_PATH).handler(cryptoWebService::handleGetSingleSymbolCryptoData);
    }

    public void handleGetMultipleSymbolsCryptoData(final RoutingContext context) {
        HandlerUtil.parseQueryParamRequest(
            context,
            "symbols",
            symbols -> new GetSymbolsDataRequest(new ArrayList<>(symbols)),
            this::processMultipleSymbolsRequest
        );
    }

    private void processMultipleSymbolsRequest(final RoutingContext context, final GetSymbolsDataRequest request) {
        LOG.info("Received request for crypto data with multiple symbols: {}", request);

        cryptoService.handleGetMultipleSymbolsCryptoData(request)
            .onSuccess(result -> context.json(JsonObject.mapFrom(result)))
            .onFailure(failure -> HandlerUtil.handleFailure(context, failure));
    }

    public void handleGetSingleSymbolCryptoData(final RoutingContext context) {
        HandlerUtil.parseRequest(context, GetSymbolDataRequest.class, this::processSingleSymbolRequest, "symbol");
    }

    private void processSingleSymbolRequest(final RoutingContext context, final GetSymbolDataRequest request) {
        LOG.info("Received request for crypto data with single symbol: {}", request);

        cryptoService.handleGetSingleSymbolCryptoData(request)
            .onSuccess(result -> context.json(JsonObject.mapFrom(result)))
            .onFailure(failure -> HandlerUtil.handleFailure(context, failure));
    }
}
