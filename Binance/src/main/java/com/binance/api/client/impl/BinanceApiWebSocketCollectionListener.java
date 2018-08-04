package com.binance.api.client.impl;

import java.io.IOException;
import java.util.List;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.domain.event.AllMarketTickersEvent;
import com.binance.api.client.exception.BinanceApiException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Binance API WebSocket listener.
 */
public class BinanceApiWebSocketCollectionListener<T> extends WebSocketListener {

	private BinanceApiCallback<T> callback;

	private boolean closing = false;

	public BinanceApiWebSocketCollectionListener(BinanceApiCallback<T> callback) {
		this.callback = callback;
	}

	@Override
	public void onMessage(WebSocket webSocket, String text) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			TypeReference<List<AllMarketTickersEvent>> mapType = new TypeReference<List<AllMarketTickersEvent>>() {
			};

			T event = null;

			// event = mapper.readValue(text,
			// mapper.getTypeFactory().constructCollectionType(List.class,
			// AllMarketTickersEvent.class));

			event = mapper.readValue(text, mapType);

			callback.onResponse(event);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BinanceApiException(e);
		}
	}

	@Override
	public void onClosing(final WebSocket webSocket, final int code, final String reason) {
		closing = true;
	}

	@Override
	public void onFailure(WebSocket webSocket, Throwable t, Response response) {
		if (!closing) {
			callback.onFailure(t);
		}
	}
}