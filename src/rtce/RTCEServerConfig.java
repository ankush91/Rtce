package rtce;

import java.io.File;

public class RTCEServerConfig {

	private static File configFile;
	private static File authFile;
	private static String encoding;
	
	
	public static File getConfigFile() {
		return configFile;
	}
	
	public static File getAuthFile() {
		return authFile;
	}
	
	public static String getEncoding(){
		return encoding;
	}
}
