package rtce.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	
	//The directory storing all of the documents
	private static File documentDir;
	
	//The filename extension for documents
	private static String fileExt;
	
	//The filename for the permissions document
	private static String permissions;
	
	//The version number
	private static final byte versionMajor = 0;
	private static final byte versionMinor = 1;
	private static final byte versionSub = 0;
	private static final byte versionExtend = 0;
	
	//The list of valid port numbers
	private static ArrayList<Integer> portNumbers = new ArrayList<Integer>();
	
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
		int minPort = 50000, maxPort = 60000;
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
				}else if(line.startsWith("doc-dir:")){
					documentDir = new File(line.split("doc-dir:")[1].trim());
				}else if(line.startsWith("min-port:")){
					minPort = Integer.parseInt(line.split("min-port:")[1].trim());
				}else if(line.startsWith("max-port:")){
					maxPort = Integer.parseInt(line.split("max-port:")[1].trim());
				}
			}
			line = reader.readLine();
		}
		for(int i = minPort; i < maxPort; i++){
			portNumbers.add(i);
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
	 * Get the directory containing the documents
	 * @return the directory containing the documents
	 */
	public static File getDocumentDir() {
		return documentDir;
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

	/**
	 * Get the file extension for documents
	 * @return the file extension
	 */
	public static String getFileExt() {
		return fileExt;
	}

	/**
	 * Get the filename of the permissions document
	 * @return the filename of the permissions document
	 */
	public static String getPermissions() {
		return permissions;
	}
	
	/**
	 * Create a new user
	 * @param username - the new username
	 * @param password - the new password
	 * @return true if user created, false otherwise
	 */
	public static boolean createAuthor(String username, String password){
		if(username == null || username.trim().length() == 0 || authMap.containsKey(username)){
			return false;
		}else if(password != null || password.trim().length() == 0){
			return false;
		}else{
			try {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(authFile, true), RTCEConstants.getRtcecharset()));
				writer.append("\r\nUsername: " + username + "\r\nPassword: " + password);
				writer.flush();
				writer.close();
				authMap.put(username, password);
				return true;
			} catch (IOException e) {
				return false;
			}
		}
	}

	public static byte getVersionmajor() {
		return versionMajor;
	}

	public static byte getVersionminor() {
		return versionMinor;
	}

	public static byte getVersionsub() {
		return versionSub;
	}

	public static byte getVersionextend() {
		return versionExtend;
	}
	
	/**
	 * Get the version 
	 * @return 4 bytes representing the version
	 */
	public static byte[] getVersion(){
		byte version[] = new byte[4];
		version[0] = versionMajor;
		version[1] = versionMinor;
		version[2] = versionSub;
		version[3] = versionExtend;
		return version;
	}

	public static ArrayList<Integer> getPortNumbers() {
		return portNumbers;
	}

}
