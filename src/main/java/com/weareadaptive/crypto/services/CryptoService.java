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

        return Future.succeededFuture(request)
            .compose(req -> {
                ValidationResult validationResult = getSymbolsValidator.validate(req);
                return validationResult.resultCode() == ValidationResultCode.SUCCESS
                    ? Future.succeededFuture(req)
                    : Future.failedFuture(validationResult);
            })
            .compose(req -> binanceApiClient.getDataForSymbolsWithLib(req.symbols()))
            .map(HandlerUtil::parseSymbolsData);
    }

    public Future<GetSymbolDataResponse> handleGetSingleSymbolCryptoData(final GetSymbolDataRequest request) {
        LOG.info("Handling Get Crypto Data request for single symbol: {}", request);

        return Future.succeededFuture(request)
            .compose(req -> {
                ValidationResult validationResult = getSymbolValidator.validate(req);
                return validationResult.resultCode() == ValidationResultCode.SUCCESS
                    ? Future.succeededFuture(req)
                    : Future.failedFuture(validationResult);
                // make sure to had log here when you get issues with studff
            })
            .compose(req -> binanceApiClient.getDataForSymbolWithLib(req.symbol()))
            .map(HandlerUtil::parseSymbolData);
    }
}
