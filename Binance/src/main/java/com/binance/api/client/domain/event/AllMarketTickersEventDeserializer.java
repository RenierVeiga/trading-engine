package com.binance.api.client.domain.event;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Custom deserializer for an AllMarketTickersEvent, since the streaming API
 * returns an object in the format {"a":"symbol","f":"free","l":"locked"}, which
 * is different than the format used in the REST API.
 */
public class AllMarketTickersEventDeserializer extends JsonDeserializer<AllMarketTickersEvent> {

	@Override
	public AllMarketTickersEvent deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		ObjectCodec oc = jp.getCodec();
		JsonNode node = oc.readTree(jp);
		final String eventType = node.get("e").asText();
		final long eventTime = node.get("E").asLong();
		final String symbol = node.get("s").asText();

		final double priceChange = node.get("p").asDouble();
		final double priceChangePercent = node.get("P").asDouble();
		final double weightedAveragePrice = node.get("w").asDouble();
		final double previousDaysClosePrice = node.get("x").asDouble();
		final double currentDaysClosePrice = node.get("c").asDouble();
		final double closeTradesQuantity = node.get("Q").asDouble();
		final double bestAskPrice = node.get("a").asDouble();
		final double bestAskQuantity = node.get("A").asDouble();
		final double bestBidPrice = node.get("b").asDouble();
		final double bestBidQuantity = node.get("B").asDouble();
		final double openPrice = node.get("o").asDouble();
		final double highPrice = node.get("h").asDouble();
		final double lowPrice = node.get("l").asDouble();
		final long totalTradedBaseAssetVolume = node.get("v").asLong();
		final long totalTradedQuoteAssetVolume = node.get("q").asLong();
		final long statisticesOpenTime = node.get("O").asLong();
		final long statisticesCloseTime = node.get("C").asLong();
		final long firstTradeId = node.get("F").asLong();
		final long lastTradeId = node.get("L").asLong();
		final long totalNumberOfTrades = node.get("n").asLong();

		AllMarketTickersEvent allMarketTickersEvent = new AllMarketTickersEvent();
		allMarketTickersEvent.setEventType(eventType);
		allMarketTickersEvent.setEventTime(eventTime);
		allMarketTickersEvent.setSymbol(symbol);
		allMarketTickersEvent.setPriceChange(priceChange);
		allMarketTickersEvent.setPriceChangePercent(priceChangePercent);
		allMarketTickersEvent.setWeightedAveragePrice(weightedAveragePrice);
		allMarketTickersEvent.setPreviousDaysClosePrice(previousDaysClosePrice);
		allMarketTickersEvent.setCurrentDaysClosePrice(currentDaysClosePrice);
		allMarketTickersEvent.setCloseTradesQuantity(closeTradesQuantity);
		allMarketTickersEvent.setBestAskPrice(bestAskPrice);
		allMarketTickersEvent.setBestAskQuantity(bestAskQuantity);
		allMarketTickersEvent.setBestBidPrice(bestBidPrice);
		allMarketTickersEvent.setBestBidQuantity(bestBidQuantity);
		allMarketTickersEvent.setOpenPrice(openPrice);
		allMarketTickersEvent.setHighPrice(highPrice);
		allMarketTickersEvent.setLowPrice(lowPrice);
		allMarketTickersEvent.setTotalTradedBaseAssetVolume(totalTradedBaseAssetVolume);
		allMarketTickersEvent.setTotalTradedQuoteAssetVolume(totalTradedQuoteAssetVolume);
		allMarketTickersEvent.setStatisticesOpenTime(statisticesOpenTime);
		allMarketTickersEvent.setStatisticesCloseTime(statisticesCloseTime);
		allMarketTickersEvent.setFirstTradeId(firstTradeId);
		allMarketTickersEvent.setLastTradeId(lastTradeId);
		allMarketTickersEvent.setTotalNumberOfTrades(totalNumberOfTrades);

		return allMarketTickersEvent;
	}
}