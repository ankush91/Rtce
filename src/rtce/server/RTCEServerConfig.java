package rtce.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rtce.RTCEConstants;

public class RTCEServerConfig {

	//The file holding configuration information for the server
	private static File configFile;
	
	//The file containing authentication information
	private static File authFile;
	
	//The map of usernames to authentication strings
	private static HashMap<String, String> authMap;
	
	//The identifier of the server host
	private static String hostKey;
	
	//The file containing the list of valid encryption options
	private static File encryptFile;
	
	//The file containing the list of valid generic options
	private static File optionFile;
	
	//The list of valid encryption options
	private static ArrayList<String> validEncrypts;
	
	//The list of valid generic options
	private static ArrayList<String> validOpts;
	
	/**
	 * Initialize the server from the configuration file
	 * @param configPath - the path to the configuration file
	 */
	public static void init(String configPath){
		configFile = new File(configPath);
		authMap = new HashMap<String, String>();
		validEncrypts = new ArrayList<String>();
		validOpts = new ArrayList<String>();
	}
	
	/**
	 * Get the server configuration file
	 * @return the configuration file
	 */
	public static File getConfigFile() {
		return configFile;
	}
	
	/**
	 * Get the authentication file
	 * @return the file containing user authentications
	 */
	public static File getAuthFile() {
		return authFile;
	}

	/**
	 * Get the map of usernames to authentication strings
	 * @return a map of username Strings to authentication Strings
	 */
	public static Map<String, String> getAuthMap() {
		return authMap;
	}
	
	/**
	 * Read in the authentication data, populating the authentication map
	 * @throws IOException if cannot read the authentication file
	 */
	public static void readInAuths() throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(authFile), RTCEConstants.getRtcecharset()));
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

	/**
	 * Return the host identification key
	 * @return the host identification key as a string
	 */
	public static String getHostKey() {
		return hostKey;
	}

	/**
	 * Get the file indicating valid encryption options
	 * @return the file listing valid encryption options
	 */
	public static File getEncryptFile() {
		return encryptFile;
	}

	/**
	 * Get the file indicating valid generic options
	 * @return the file listing valid generic options
	 */
	public static File getOptionFile() {
		return optionFile;
	}

	/**
	 * Get the list of allowable encryption options
	 * @return the list of encryption options supported by the server
	 */
	public static List<String> getValidEncrypts() {
		return validEncrypts;
	}

	/**
	 * Get the list of allowable generic options
	 * @return the list of generic options supported by the server
	 */
	public static List<String> getValidOpts() {
		return validOpts;
	}
	
	
}
