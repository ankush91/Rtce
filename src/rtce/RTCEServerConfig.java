package rtce;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class RTCEServerConfig {

	private static File configFile;
	private static File authFile;
	private static HashMap<String, String> authMap;
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

	public static HashMap<String, String> getAuthMap() {
		return authMap;
	}
	
	public static void readInAuths() throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(authFile), encoding));
		String line = reader.readLine();
		String uname, password;
		while(line != null){
			if(line.startsWith("Username: ")){
				uname = line.split("Username: ")[1];
				line = reader.readLine();
				password = line.split("Password: ")[1];
				authMap.put(uname, password);
			}
			line = reader.readLine();
		}
		reader.close();
	}
}
