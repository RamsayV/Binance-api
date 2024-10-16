package com.weareadaptive.crypto;

import com.google.common.truth.Truth;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
    void receiveSingleSymbolInfo(Vertx vertx, VertxTestContext testContext)
    {

        JsonObject expectedResponse = new JsonObject();
        expectedResponse.put("symbol", "XLMUSDT");
        expectedResponse.put("priceChange", "0.00130000");
        expectedResponse.put("priceChangePercent", "1.401");
        expectedResponse.put("weightedAvgPrice", "0.09292165");
        expectedResponse.put("openPrice", "0.09280000");
        expectedResponse.put("highPrice", "0.09460000");
        expectedResponse.put("lowPrice", "0.09090000");
        expectedResponse.put("lastPrice", "0.09410000");
        expectedResponse.put("volume", "65132227.00000000");
        expectedResponse.put("quoteVolume", "6052193.97640000");
        expectedResponse.put("openTime", 1728998940000L);
        expectedResponse.put("closeTime", 1729085373799L);
        expectedResponse.put("firstId", 156228945);
        expectedResponse.put("lastId", 156276674);
        expectedResponse.put("count", 47730);

        final String SYMBOL_PATH = "/api/crypto/:symbol";
        final HttpClient client = vertx.createHttpClient();
        final int port = webVerticle.boundPort();
        final String requestUri = SYMBOL_PATH.replace(":symbol", "XLMUSDT");
        client.request(HttpMethod.GET, port, "localhost", requestUri)
            .compose(req -> req.send().compose(HttpClientResponse::body))
            .onComplete(testContext.succeeding(buffer -> testContext.verify(() ->
            {
                assertThat(buffer.toString()).isEqualTo(expectedResponse.toString());
                testContext.completeNow();
            })));
        ;
    }
}
