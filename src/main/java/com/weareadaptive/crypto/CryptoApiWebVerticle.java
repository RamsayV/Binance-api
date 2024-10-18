package com.weareadaptive.crypto;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import com.weareadaptive.crypto.services.CryptoWebService;
import com.weareadaptive.crypto.services.CryptoService;

public class CryptoApiWebVerticle extends AbstractVerticle {
    private final int configuredPort;
    private HttpServer httpServer;
    private  CryptoService cryptoService;

    public CryptoApiWebVerticle(final int configuredPort) {
        this.configuredPort = configuredPort;
    }

    public int boundPort() {
        return httpServer.actualPort();
    }

    @Override
    public void start(final Promise<Void> startPromise) {
        this.cryptoService = new CryptoService(vertx);

        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        CryptoWebService.registerService(router, cryptoService);

        httpServer = vertx.createHttpServer();
        httpServer
            .requestHandler(router)
            .listen(configuredPort)
            .<Void>mapEmpty()
            .onComplete(startPromise);
    }
}
