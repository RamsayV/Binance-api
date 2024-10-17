package com.weareadaptive.crypto;

import com.weareadaptive.crypto.dto.GetSymbolDataRequest;
import com.weareadaptive.crypto.dto.GetSymbolDataResponse;
import com.weareadaptive.crypto.dto.GetSymbolsResponseData;
import com.weareadaptive.crypto.model.CryptoTickerData;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@ExtendWith(VertxExtension.class)
public class CryptoApiWebVerticleTest
{
    private final CryptoApiWebVerticle webVerticle;

    public CryptoApiWebVerticleTest()
    {
        this.webVerticle = new CryptoApiWebVerticle(0);
    }

    @BeforeEach
    void beforeEach(Vertx vertx, VertxTestContext testContext)
    {
        vertx.deployVerticle(webVerticle).onComplete(f -> testContext.completeNow(), testContext::failNow);
    }

    @Test
    void receiveHelloWorld(Vertx vertx, VertxTestContext testContext)
    {

        final HttpClient client = vertx.createHttpClient();
        final int port = webVerticle.boundPort();

        client.request(HttpMethod.GET, port, "localhost", "/api/crypto")
            .compose(req -> req.send().compose(HttpClientResponse::body))
            .onComplete(testContext.succeeding(buffer -> testContext.verify(() ->
            {
                assertThat(buffer.toString()).isEqualTo("Hello world!");
                testContext.completeNow();
            })));
        ;
    }

    @Test
    void receiveSingleSymbolInfoTest(Vertx vertx, VertxTestContext testContext)
    {
        GetSymbolDataRequest request = new GetSymbolDataRequest("XLMUSDT");

        final String SYMBOL_PATH = "/api/crypto/:symbol";
        final HttpClient client = vertx.createHttpClient();
        final int port = webVerticle.boundPort();
        final String requestUri = SYMBOL_PATH.replace(":symbol", request.symbol());

        client.request(HttpMethod.GET, port, "localhost", requestUri)
            .compose(req -> req.send().compose(HttpClientResponse::body))
            .onComplete(testContext.succeeding(buffer -> testContext.verify(() ->
            {
                GetSymbolDataResponse response = JsonObject.mapFrom(new JsonObject(buffer)).mapTo(GetSymbolDataResponse.class);
                CryptoTickerData cryptoTickerData = response.cryptoTickerData();

                assertThat(cryptoTickerData).isNotNull();
                assertThat(cryptoTickerData.symbol()).isEqualTo(request.symbol());

                assertThat(cryptoTickerData.lastPrice()).isInstanceOf(BigDecimal.class);
                assertThat(cryptoTickerData.priceChange()).isInstanceOf(BigDecimal.class);
                assertThat(cryptoTickerData.priceChangePercent()).isInstanceOf(BigDecimal.class);
                assertThat(cryptoTickerData.highPrice()).isInstanceOf(BigDecimal.class);
                assertThat(cryptoTickerData.lowPrice()).isInstanceOf(BigDecimal.class);
                assertThat(cryptoTickerData.volume()).isInstanceOf(BigDecimal.class);
                assertThat(cryptoTickerData.closeTime()).isInstanceOf(Instant.class);

                assertThat(cryptoTickerData.lastPrice()).isGreaterThan(BigDecimal.ZERO);
                assertThat(cryptoTickerData.highPrice()).isGreaterThan(cryptoTickerData.lowPrice());
                assertThat(cryptoTickerData.volume()).isGreaterThan(BigDecimal.ZERO);
                assertThat(cryptoTickerData.closeTime()).isLessThan(Instant.now());

                testContext.completeNow();
            })));
    }


    @Test
    void receiveMultipleSymbolsInfoTest(Vertx vertx, VertxTestContext testContext) {
        List<String> symbols = Arrays.asList("BTCUSDT", "ETHUSDT", "XLMUSDT");
        String symbolsParam = String.join(",", symbols);

        final String CRYPTOS_PATH = "/api/crypto";
        final HttpClient client = vertx.createHttpClient();
        final int port = webVerticle.boundPort();
        final String requestUri = CRYPTOS_PATH + "?symbols=" + symbolsParam;

        client.request(HttpMethod.GET, port, "localhost", requestUri)
            .compose(req -> req.send().compose(HttpClientResponse::body))
            .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
                GetSymbolsResponseData response = JsonObject.mapFrom(new JsonObject(buffer)).mapTo(GetSymbolsResponseData.class);
                List<CryptoTickerData> cryptoTickerDataList = response.symbolsTickerData();

                assertThat(cryptoTickerDataList).isNotNull();

                for (CryptoTickerData cryptoTickerData : cryptoTickerDataList) {
                    assertThat(cryptoTickerData).isNotNull();
                    assertThat(symbols).contains(cryptoTickerData.symbol());

                    assertThat(cryptoTickerData.lastPrice()).isInstanceOf(BigDecimal.class);
                    assertThat(cryptoTickerData.priceChange()).isInstanceOf(BigDecimal.class);
                    assertThat(cryptoTickerData.priceChangePercent()).isInstanceOf(BigDecimal.class);
                    assertThat(cryptoTickerData.highPrice()).isInstanceOf(BigDecimal.class);
                    assertThat(cryptoTickerData.lowPrice()).isInstanceOf(BigDecimal.class);
                    assertThat(cryptoTickerData.volume()).isInstanceOf(BigDecimal.class);
                    assertThat(cryptoTickerData.closeTime()).isInstanceOf(Instant.class);

                    assertThat(cryptoTickerData.lastPrice()).isGreaterThan(BigDecimal.ZERO);
                    assertThat(cryptoTickerData.highPrice()).isGreaterThan(cryptoTickerData.lowPrice());
                    assertThat(cryptoTickerData.volume()).isGreaterThan(BigDecimal.ZERO);
                    assertThat(cryptoTickerData.closeTime()).isLessThan(Instant.now());
                }

                testContext.completeNow();
            })));
    }

}
