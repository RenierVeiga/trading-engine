package com.binance.api.examples;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.connect.AccountInfo;

import reports.Report;

/**
 * Illustrates how to use the klines/candlesticks event stream to create a local
 * cache of bids/asks for a symbol.
 */
public abstract class CandleSticksCache {

    BinanceApiWebSocketClient client = AccountInfo.getSocketClient();

    /**
     * Key is the start/open time of the candle, and the value contains candlestick
     * date.
     */
    private SortedMap<Long, Candlestick> candlesticksCache;
    private List<Candlestick> candleStickList;
    private double closePrice;
    protected Closeable clientCloseable;
    private String symbol;

    public CandleSticksCache(String symbol, CandlestickInterval interval) {
	this.symbol = symbol;
	initializeCandlestickCache(symbol, interval);
	startCandlestickEventStreaming(symbol, interval);
    }

    /**
     * Initializes the candlestick cache by using the REST API.
     */
    private void initializeCandlestickCache(String symbol, CandlestickInterval interval) {
	candleStickList = Collections.synchronizedList(new ArrayList<Candlestick>(
		AccountInfo.getRestClient().getCandlestickBars(symbol.toUpperCase(), interval)));

	this.candlesticksCache = Collections.synchronizedSortedMap(new TreeMap<>());
	for (Candlestick candlestickBar : candleStickList) {
	    candlesticksCache.put(candlestickBar.getOpenTime(), candlestickBar);
	}
	closePrice = Double.parseDouble(getLastCandle().getClose());
    }

    /**
     * Begins streaming of depth events.
     */
    private void startCandlestickEventStreaming(String symbol, CandlestickInterval interval) {

	clientCloseable = client.onCandlestickEvent(symbol.toLowerCase(), interval, response -> {
	    Long openTime = response.getOpenTime();
	    Candlestick updateCandlestick = candlesticksCache.get(openTime);
	    if (updateCandlestick == null) {
		// new candlestick
		updateCandlestick = new Candlestick();
	    }
	    // update candlestick with the stream data
	    updateCandlestick.setOpenTime(response.getOpenTime());
	    updateCandlestick.setOpen(response.getOpen());
	    updateCandlestick.setLow(response.getLow());
	    updateCandlestick.setHigh(response.getHigh());
	    updateCandlestick.setClose(response.getClose());
	    updateCandlestick.setCloseTime(response.getCloseTime());
	    updateCandlestick.setVolume(response.getVolume());
	    updateCandlestick.setNumberOfTrades(response.getNumberOfTrades());
	    updateCandlestick.setQuoteAssetVolume(response.getQuoteAssetVolume());
	    updateCandlestick.setTakerBuyQuoteAssetVolume(response.getTakerBuyQuoteAssetVolume());
	    updateCandlestick.setTakerBuyBaseAssetVolume(response.getTakerBuyQuoteAssetVolume());

	    // Store the updated candlestick in the cache

	    candlesticksCache.put(openTime, updateCandlestick);
	    candleStickList = new ArrayList<Candlestick>(candlesticksCache.values());
	    closePrice = Double.parseDouble(response.getClose());
	    onCandleStickEvent();
	});
    }

    protected abstract void onCandleStickEvent();

    /**
     * @return a klines/candlestick cache, containing the open/start time of the
     *         candlestick as the key, and the candlestick data as the value.
     */
    public Map<Long, Candlestick> getCandlesticksCache() {
	return candlesticksCache;
    }

    public double getClosePrice() {
	return closePrice;
    }

    public void closeClient() {
	try {
	    clientCloseable.close();
	} catch (IOException e) {
	    e.printStackTrace();
	    Report.createReport("Failed to close socket for: " + symbol);
	    Report.createReport("Error: " + e.getMessage());
	}
    }

    public String getSymbol() {
	return symbol;
    }

    public List<Candlestick> getCandleStickList() {
	return candleStickList;
    }

    public Candlestick getLastCandle() {
	if (candleStickList.size() > 0) {
	    return candleStickList.get(candleStickList.size() - 1);
	} else {
	    return null;
	}
    }

    public Candlestick getPriorCandle() {
	if (candleStickList.size() > 1) {
	    return candleStickList.get(candleStickList.size() - 2);
	} else {
	    return null;
	}
    }

}
