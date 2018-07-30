package properties;

import java.io.*;

public class Properties {
	static String key;

	static String secret;
	
	/*
	 * This value indicates if we are running on auto stop sell mode, or fully
	 * automated mode. sellOnlyMode = true means we are not ready to run fully
	 * automated.
	 */
	private static boolean sellOnlyMode = true;
	private static final Properties instance = new Properties();
	private static java.util.Properties p = new java.util.Properties();

	private Properties() {}

	public static Properties getInstance() {
		try (InputStream inputStream = new FileInputStream("C:\\Users\\renie\\git\\trading-engine\\AccountInfo\\src\\properties\\config.properties")) {
			p.load(inputStream);
			// key = value from txt file.
			key  = p.getProperty("key");
			// secret = value from txt file.
			secret = p.getProperty("secret");
			// sellOnlyMode = value from txt file.
			if (p.getProperty("sellOnlyMode").equals("false")) {
				sellOnlyMode = false;
			} else {
				sellOnlyMode = true;
			}
			inputStream.close();
		} catch(IOException ex){
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
}