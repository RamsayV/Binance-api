package com.weareadaptive.crypto.services;

import com.weareadaptive.crypto.Validation.GetSymbolValidator;
import com.weareadaptive.crypto.Validation.GetSymbolsValidator;
import com.weareadaptive.crypto.Validation.ValidationResult;
import com.weareadaptive.crypto.Validation.ValidationResultCode;
import com.weareadaptive.crypto.api.BinanceApiClient;
import com.weareadaptive.crypto.dto.GetSymbolDataRequest;
import com.weareadaptive.crypto.dto.GetSymbolDataResponse;
import com.weareadaptive.crypto.dto.GetSymbolsDataRequest;
import com.weareadaptive.crypto.dto.GetSymbolsResponseData;
import com.weareadaptive.crypto.util.HandlerUtil;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CryptoService {
    private static final Logger LOG = LoggerFactory.getLogger(CryptoService.class);
    private final BinanceApiClient binanceApiClient;
    private final GetSymbolValidator getSymbolValidator;
    private final GetSymbolsValidator getSymbolsValidator;

    public CryptoService(final Vertx vertx) {
        this.binanceApiClient = new BinanceApiClient(vertx);
        this.getSymbolValidator = new GetSymbolValidator();
        this.getSymbolsValidator = new GetSymbolsValidator();
    }

    public Future<GetSymbolsResponseData> handleGetMultipleSymbolsCryptoData(final GetSymbolsDataRequest request) {
        LOG.info("Handling Get Crypto Data request for multiple symbols: {}", request);

        final ValidationResult validationResult = getSymbolsValidator.validate(request);
        if (validationResult.resultCode() != ValidationResultCode.SUCCESS) {
            LOG.error("Validation failed: {}", validationResult);
            return Future.failedFuture(validationResult);
        }

        return binanceApiClient.getDataForSymbolsWithLib(request.symbols())
            .map(HandlerUtil::parseSymbolsData);
    }

    public Future<GetSymbolDataResponse> handleGetSingleSymbolCryptoData(final GetSymbolDataRequest request) {
        LOG.info("Handling Get Crypto Data request for single symbol: {}", request);

        final ValidationResult validationResult = getSymbolValidator.validate(request);
        if (validationResult.resultCode() != ValidationResultCode.SUCCESS) {
            return Future.failedFuture(validationResult);
        }

        return binanceApiClient.getDataForSymbolWithLib(request.symbol())
            .map(HandlerUtil::parseSymbolData);
    }
}
