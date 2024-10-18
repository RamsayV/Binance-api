package com.weareadaptive.crypto.model;

import java.math.BigDecimal;
import java.time.Instant;

public record CryptoTickerData(String symbol,
                               BigDecimal lastPrice,
                               BigDecimal openPrice,
                               BigDecimal priceChange,
                               BigDecimal priceChangePercent,
                               BigDecimal highPrice,
                               BigDecimal lowPrice,
                               BigDecimal volume,
                               Instant closeTime)
{
}
