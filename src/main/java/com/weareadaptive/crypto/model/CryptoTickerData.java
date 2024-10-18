package com.weareadaptive.crypto.model;

import java.math.BigDecimal;
import java.time.Instant;

public record CryptoTickerData(
    String symbol,
    double price,
    double open,
    double low,
    double high,
    Instant closeTime
) {}
