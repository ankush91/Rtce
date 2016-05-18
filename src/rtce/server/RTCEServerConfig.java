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
	 * @throws IOException - if the configuration file cannot be read
	 */
	public static void init(String configPath) throws IOException{
		configFile = new File(configPath);
		authMap = new HashMap<String, String>();
		validEncrypts = new ArrayList<String>();
		validOpts = new ArrayList<String>();
		readConfigFile();
	}
	
	/**
	 * Read the configuration file and extract values
	 * @throws IOException - if the configuration file cannot be read
	 */
	private static void readConfigFile() throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), RTCEConstants.getRtcecharset()));
		String line = reader.readLine();
		while(line != null){
			line = line.trim();
			if(!line.startsWith("#")){
				if(line.startsWith("server-host-key:")){
					hostKey = line.split("server-host-key:")[1].trim();
				}else if(line.startsWith("auth-file:")){
					authFile = new File(line.split("auth-file:")[1].trim());
					readInAuths();
				}else if(line.startsWith("encrypt-file:")){
					encryptFile = new File(line.split("encrypt-file:")[1].trim());
					readInEncrypts();
				}else if(line.startsWith("opt-file:")){
					optionFile = new File(line.split("opt-file:")[1].trim());
					readInOpts();
				}
			}
			line = reader.readLine();
		}
		reader.close();
	}
	
	/**
	 * Read the encryption file and extract values
	 * @throws IOException - if the encryption file cannot be read
	 */
	private static void readInEncrypts() throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(encryptFile), RTCEConstants.getRtcecharset()));
		String line = reader.readLine();
		while(line != null){
			line = line.trim();
			if(!line.startsWith("#") && line.length() >= 1){
				validEncrypts.add(line);
			}
		}
		reader.close();
	}
	
	/**
	 * Read the option file and extract values
	 * @throws IOException - if the option file cannot be read
	 */
	private static void readInOpts() throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(optionFile), RTCEConstants.getRtcecharset()));
		String line = reader.readLine();
		while(line != null){
			line = line.trim();
			if(!line.startsWith("#") && line.length() >= 1){
				validOpts.add(line);
			}
		}
		reader.close();
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
			line = line.trim();
			if(line.startsWith("Username:")){
				uname = line.split("Username:")[1].trim();
				line = reader.readLine();
				password = line.split("Password:")[1].trim();
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
