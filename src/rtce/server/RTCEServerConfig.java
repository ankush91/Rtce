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

/**
 * RTCEServerConfig
 * Configures the server with server side constants and static methods
 * @author Edwin Dauber, Ankush Israney, Anthony Emma, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 */
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

	//The length of time to block users (in milliseconds)
	private static long blockTime;

	//The length of time to grant tokens (in milliseconds)
	private static long tokenTime;

	//The version number
	private static final byte versionMajor = 1;
	private static final byte versionMinor = 0;
	private static final byte versionSub = 0;
	private static final byte versionExtend = 0;

	//The port number
	private static int portNumber;

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
				}else if(line.startsWith("doc-dir:")){
					documentDir = new File(line.split("doc-dir:")[1].trim());
				}else if(line.startsWith("port-num:")){
					portNumber = Integer.parseInt(line.split("port-num:")[1].trim());
				}else if(line.startsWith("doc-ext:")){
					fileExt = line.split("doc-ext:")[1].trim();
				}else if(line.startsWith("perm-doc:")){
					permissions = line.split("perm-doc:")[1].trim();
				}else if(line.startsWith("block-time:")){
					blockTime = Long.parseLong(line.split("block-time:")[1].trim());
				}else if(line.startsWith("token-time:")){
					tokenTime = Long.parseLong(line.split("token-time:")[1].trim());
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
		}else if(password == null || password.trim().length() == 0){
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

	/**
	 * Get the major version number
	 * @return the byte representing the major version number
	 */
	public static byte getVersionmajor() {
		return versionMajor;
	}

	/**
	 * Get the minor version number
	 * @return the byte representing the minor version number
	 */
	public static byte getVersionminor() {
		return versionMinor;
	}

	/**
	 * Get the sub-version number
	 * @return the byte representing the sub-version number
	 */
	public static byte getVersionsub() {
		return versionSub;
	}

	/**
	 * Get the version extension number
	 * @return the byte representing the version extension number
	 */
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

	/**
	 * Get the port number, as defined in the configuration file
	 * @return the port number
	 */
	public static int getPortNumber() {
		return portNumber;
	}

	/**
	 * Get the block time
	 * @return the block time
	 */
	public static long getBlockTime() {
		return blockTime;
	}

	/**
	 * Get the token timeout
	 * @return the token timeout
	 */
	public static long getTokenTime() {
		return tokenTime;
	}

}
