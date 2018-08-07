package com.binance.api.examples;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.connect.AccountInfo;

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
	private Map<Long, Candlestick> candlesticksCache;
	private Candlestick lastCandle;
	private double closePrice;
	private double sellTrailPrice = 0;

	public CandleSticksCache(String symbol, CandlestickInterval interval) {
		initializeCandlestickCache(symbol, interval);
		startCandlestickEventStreaming(symbol, interval);
	}

	/**
	 * Initializes the candlestick cache by using the REST API.
	 */
	private void initializeCandlestickCache(String symbol, CandlestickInterval interval) {
		List<Candlestick> candlestickBars = AccountInfo.getRestClient().getCandlestickBars(symbol.toUpperCase(),
				interval);

		this.candlesticksCache = new TreeMap<>();
		for (Candlestick candlestickBar : candlestickBars) {
			candlesticksCache.put(candlestickBar.getOpenTime(), candlestickBar);
			lastCandle = candlestickBar;
			closePrice = Double.parseDouble(lastCandle.getClose());
			sellTrailPrice = closePrice;
		}
	}

	/**
	 * Begins streaming of depth events.
	 */
	private void startCandlestickEventStreaming(String symbol, CandlestickInterval interval) {

		client.onCandlestickEvent(symbol.toLowerCase(), interval, response -> {
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
			lastCandle = updateCandlestick;
			closePrice = Double.parseDouble(lastCandle.getClose());
			if (closePrice > sellTrailPrice) {
				sellTrailPrice = closePrice;
			}
			onCandleStickEvent();
			System.out.println(updateCandlestick.toString());
		});
	}

	public double getSellTrailPrice() {
		return sellTrailPrice;
	}

	public abstract void onCandleStickEvent();

	/**
	 * @return a klines/candlestick cache, containing the open/start time of the
	 *         candlestick as the key, and the candlestick data as the value.
	 */
	public Map<Long, Candlestick> getCandlesticksCache() {
		return candlesticksCache;
	}

	public Candlestick getLastCandle() {
		return lastCandle;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public void closeClient() {
		client.close();
		client = null;
	}

}
