// package subjects;
//
// import java.util.List;
//
// import com.binance.api.client.BinanceApiWebSocketClient;
// import com.binance.api.client.domain.event.AllMarketTickersEvent;
// import com.binance.api.connect.AccountInfo;
//
// import Entities.Subject;
// import observers.AssetObserver;
// import observers.BtcUsdSignals;
//
// public class PriceStreamSubject extends Subject<AssetObserver> {
//
// private BinanceApiWebSocketClient bsc = AccountInfo.getSocketClient();
// private List<AllMarketTickersEvent> allTicker;
// private static PriceStreamSubject instance = new PriceStreamSubject();
//
// private PriceStreamSubject() {
// // Opens a socket connection and constantly updates the current price by
// // kicking off a price fetch event for all symbols on the exchange.
// System.out.println("Start Price stream subject.");
// bsc.onAllMarketTickersEvent(response -> {
// allTicker = response;
// this.updateObservers();
// });
// }
//
// @Override
// public void updateObservers() {
// System.out.println("Price Update.");
// BtcUsdSignals.getInstance().updatePrice(getPriceForSymbol("BTCUSDT"));
// for (AssetObserver item : observerMap.values()) {
// item.updatePrice(getPriceForSymbol(item.getSymbol()));
// }
// }
//
// private Double getPriceForSymbol(String symbol) {
// Double price = null;
// for (AllMarketTickersEvent item : allTicker) {
// if (item.getSymbol().equals(symbol.toUpperCase())) {
// price = item.getBestBidPrice();
// break;
// }
// }
// return price;
// }
//
// public static PriceStreamSubject getInstance() {
// return instance;
// }
//
// }
