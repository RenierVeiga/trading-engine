package com.binance.api.connect;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;

import properties.Properties;

public class AccountInfo {

	// API initialization
	private static final BinanceApiClientFactory factory = BinanceApiClientFactory
			.newInstance(Properties.getInstance().getKey(), Properties.getInstance().getSecret());

	private static final BinanceApiAsyncRestClient restAsynClient = factory.newAsyncRestClient();
	private static final BinanceApiRestClient restClient = factory.newRestClient();

	public AccountInfo() {

	}

	public static BinanceApiRestClient getRestClient() {
		return restClient;
	}

	public static BinanceApiAsyncRestClient getRestAsyncClient() {
		return restAsynClient;
	}

	public static BinanceApiWebSocketClient getSocketClient() {
		return factory.newWebSocketClient();
	}

}
