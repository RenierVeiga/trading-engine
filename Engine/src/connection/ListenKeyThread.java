package connection;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;
import com.binance.api.connect.AccountInfo;

import reports.Report;

public class ListenKeyThread extends Thread {

	public ListenKeyThread() {
	}

	public void run() {
		renewListenKey();
	}

	public void renewListenKey() {
		try {
			// Sleep for 12 hours.
			Thread.sleep(43200000);
			// Sleep for 10 minutes.
			// Thread.sleep(600000);
			// Renew the listen key.
			AccountInfo.setListenKey(AccountInfo.getRestClient().startUserDataStream());
			// Restart the Balance Stream.
			ConnectionManager.resetListenKeyConnection();
			Report.createReport("Connection reseted: " + this.toString());
			// Do it all over again.
			renewListenKey();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
				.append("Date", new Date().toString()).toString();
	}
}
