package AccountInfo;

import java.io.Closeable;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.client.domain.event.AllMarketTickersEvent;
import com.binance.api.client.impl.BinanceApiWebSocketClientImpl;
import com.binance.api.client.impl.BinanceApiWebSocketListener;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class BinanceApiWebSocketClientImpl2_0 extends BinanceApiWebSocketClientImpl{
	
	private OkHttpClient client;
	
    public BinanceApiWebSocketClientImpl2_0() {
        Dispatcher d = new Dispatcher();
        d.setMaxRequestsPerHost(100);
        this.client = new OkHttpClient.Builder().dispatcher(d).build();
    }

	
    public Closeable onSingleMarketTickerEvent(BinanceApiCallback<AllMarketTickersEvent> callback) {
        final String channel = "!ticker@arr";
        return createNewWebSocket(channel, new BinanceApiWebSocketListener<AllMarketTickersEvent>(callback));
    }
    
    private Closeable createNewWebSocket(String channel, BinanceApiWebSocketListener<?> listener) {
        String streamingUrl = String.format("%s/%s", BinanceApiConstants.WS_API_BASE_URL, channel);
        Request request = new Request.Builder().url(streamingUrl).build();
        final WebSocket webSocket = client.newWebSocket(request, listener);
        return () -> {
            final int code = 1000;
            listener.onClosing(webSocket, code, null);
            webSocket.close(code, null);
            listener.onClosed(webSocket, code, null);
        };
    }
}
