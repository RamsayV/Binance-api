package com.weareadaptive.crypto.dto;

import com.weareadaptive.crypto.model.CryptoTickerData;

import java.util.ArrayList;

public record GetSymbolsResponseData(ArrayList<CryptoTickerData> symbolsTickerData)
{
}
