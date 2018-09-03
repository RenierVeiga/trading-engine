package com.binance.api.examples;

import java.io.Closeable;

import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.connect.AccountInfo;

/**
 * Illustrates how to use the klines/candlesticks event stream to create a local
 * cache of bids/asks for a symbol.
 */
public abstract class CandleSticksStream {

    BinanceApiWebSocketClient client = AccountInfo.getSocketClient();

    /**
     * Key is the start/open time of the candle, and the value contains candlestick
     * date.
     */

    protected Closeable clientCloseable;
    private String symbol;

    public CandleSticksStream(String symbol, CandlestickInterval interval) {
	this.symbol = symbol;
	startCandlestickEventStreaming(symbol, interval);
    }

    /**
     * Begins streaming of depth events.
     */
    private void startCandlestickEventStreaming(String symbol, CandlestickInterval interval) {

	clientCloseable = client.onCandlestickEvent(symbol.toLowerCase(), interval, response -> {
	    onCandleStickEvent();
	});
    }

    protected abstract void onCandleStickEvent();

    public abstract void closeClient();

    public String getSymbol() {
	return symbol;
    }

}
