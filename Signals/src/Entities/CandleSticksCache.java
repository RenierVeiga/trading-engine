package Entities;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	List<Candlestick> candleStickList = new ArrayList<Candlestick>(
		AccountInfo.getRestClient().getCandlestickBars(symbol.toUpperCase(), interval));

	this.candlesticksCache = Collections.synchronizedSortedMap(new TreeMap<>());
	for (Candlestick candlestickBar : candleStickList) {
	    candlesticksCache.put(candlestickBar.getOpenTime(), candlestickBar);
	}
    }

    /**
     * Begins streaming of depth events.
     */
    private void startCandlestickEventStreaming(String symbol, CandlestickInterval interval) {

	clientCloseable = client.onCandlestickEvent(symbol.toLowerCase(), interval, response -> {
	    Candlestick updateCandlestick = candlesticksCache.get(response.getOpenTime());
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
	    candlesticksCache.put(updateCandlestick.getOpenTime(), updateCandlestick);
	    onCandleStickEvent();
	});
    }

    protected abstract void onCandleStickEvent();

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

    public synchronized SortedMap<Long, Candlestick> getCandlesticksCache() {
	return candlesticksCache;
    }

}
