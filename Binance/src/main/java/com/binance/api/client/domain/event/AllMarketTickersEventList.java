package com.binance.api.client.domain.event;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllMarketTickersEventList {

	@JsonDeserialize(contentUsing = AllMarketTickersEventDeserializer.class)
	private List<AllMarketTickersEvent> AllMarketTickersEventList;

	public List<AllMarketTickersEvent> getAllMarketTickersEventList() {
		return AllMarketTickersEventList;
	}

	public void setAllMarketTickersEventList(List<AllMarketTickersEvent> allMarketTickersEventList) {
		AllMarketTickersEventList = allMarketTickersEventList;
	}

}
