package com.weareadaptive.crypto;

import io.vertx.core.Vertx;

import java.util.ArrayList;

public class CryptoApiMain
{
    private static final int CONFIGURED_PORT = 8080;

    public static void main(final String[] args)
    {
        System.out.println("Creating Crypto API Web Server");


        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new CryptoApiWebVerticle(CONFIGURED_PORT))
            .onSuccess(id -> System.out.println("Deployed Crypto API Web Server"))
            .onFailure(err -> System.out.println("Failed to deploy Crypto API Web Server: " + err.getMessage()));

//        System.out.println("Deployed Crypto API Web Server");
//
//
//        BinanceExample.getDataForSymbolWithLib(vertx,"ETHBTC")
//            .onSuccess(result -> System.out.println("Intra-day trading data for ETHBTC (lib): " + result))
//            .onFailure(error -> System.out.println("Error (lib): " + error.getMessage()));
//
//
//
//        BinanceExample.getDataForSymbolsWithLib(vertx,symbols)
//            .onSuccess(result ->  System.out.println("Intra-day trading data for BTCUSDT + ETHBTC : " + result))
//            .onFailure(error -> System.out.println("Error (lib): " + error.getMessage()));
//        ArrayList<String> symbols = new ArrayList<>();
//        symbols.add("BTCUSDT");
//        symbols.add("ETHBTC");
    }
}
