package properties;

import java.io.IOException;
import java.io.InputStream;

public class Properties {
	String key, secret;

	/*
	 * This value indicates if we are running on auto stop sell mode, or fully
	 * automated mode. sellOnlyMode = true means we are not ready to run fully
	 * automated.
	 */
	boolean sellOnlyMode = true;
	private static final Properties instance = new Properties();
	InputStream input = null;

	Properties() {
//		try {
			
			
//			// Read properties txt file and set variables here.
//			input = FileInputStream("config.properties");
//			instance.load(input);
//			// key = value from txt file.
//			key = instance.getProperty("key");
//			// secret = value from txt file.
//			secret = instance.getProperty("secret");
//			// sellOnlyMode = value from txt file.
//			sellOnlyMode = Boolean.parseBoolean(instance.getProperty("sellOnlyMode"));
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		} finally {
//			if (input != null) {
//				try {
//					input.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}

	public static Properties getInstance() {
		return instance;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
