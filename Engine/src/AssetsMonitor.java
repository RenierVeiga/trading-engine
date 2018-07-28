
import java.util.HashMap;

import AccountInfo.AccountInfo;

/**
 * @author Renier Veiga
 * 
 *         Date: Jul 28, 2018
 * 
 *         For each asset create a tread to watch each asset and liquidate it if
 *         necessary.
 *
 */

public class AssetsMonitor {

	AccountInfo acm = new AccountInfo();
	HashMap<String, AssetWatcher> assetMap = new HashMap<String, AssetWatcher>();

	public AssetsMonitor() {

	}

	public void startWatchforAssets() {

		for (String asset : acm.getAssets()) {
			if (!assetMap.containsKey(asset)) {
				assetMap.put(asset, new AssetWatcher(asset));
			}
		}
	}

}
