package properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Properties {
	private static String key, secret;
	private static Double trailingPercent;

	/*
	 * This value indicates if we are running on auto stop sell mode, or fully
	 * automated mode. sellOnlyMode = true means we are not ready to run fully
	 * automated.
	 */
	private static boolean sellOnlyMode = true;
	private static final Properties instance = new Properties();
	private static java.util.Properties p = new java.util.Properties();

	private Properties() {
	}

	public static Properties getInstance() {
		try (InputStream inputStream = new FileInputStream(
				"C:\\Users\\renie\\git\\trading-engine\\AccountProperties\\src\\properties\\config.properties")) {
			p.load(inputStream);
			// key = value from txt file.
			key = p.getProperty("key");
			// secret = value from txt file.
			secret = p.getProperty("secret");
			// sellOnlyMode = value from txt file.
			trailingPercent = Double.parseDouble(p.getProperty("trailingPercent"));
			if (p.getProperty("sellOnlyMode").equals("false")) {
				sellOnlyMode = false;
			} else {
				sellOnlyMode = true;
			}
			inputStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return instance;
	}

	public String getKey() {
		return key;
	}

	public String getSecret() {
		return secret;
	}

	public boolean getMode() {
		return sellOnlyMode;
	}

	public void setProperty(String key, String value) {
		p.setProperty(key, value);
	}

	public Double getTrailingPercent() {
		return trailingPercent;
	}
}