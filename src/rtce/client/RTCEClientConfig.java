package rtce.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rtce.RTCEConstants;

public class RTCEClientConfig {

	//The file holding configuration information for the client
	private static File configFile;
		
	//The file containing the ordered list of desired encryption options
	private static File encryptFile;
		
	//The file containing the list of desired generic options
	private static File optionFile;
	
	//The ordered list of desired encryption options
	private static ArrayList<String> desiredEncrypts;
		
	//The list of desired generic options
	private static ArrayList<String> desiredOpts;
	
	//The version number
	private static final byte versionMajor = 0;
	private static final byte versionMinor = 1;
	private static final byte versionSub = 0;
	private static final byte versionExtend = 0;
	
	/**
	 * Initialize the server from the configuration file
	 * @param configPath - the path to the configuration file
	 * @throws IOException - if the configuration file cannot be read
	 */
	public static void init(String configPath) throws IOException{
		configFile = new File(configPath);
		desiredEncrypts = new ArrayList<String>();
		desiredOpts = new ArrayList<String>();
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
				if(line.startsWith("encrypt-file:")){
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
				desiredEncrypts.add(line);
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
				desiredOpts.add(line);
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
	 * Get the file indicating desired encryption options, in preference order
	 * @return the file listing desired encryption options
	 */
	public static File getEncryptFile() {
		return encryptFile;
	}

	/**
	 * Get the file indicating desired generic options
	 * @return the file listing desired generic options
	 */
	public static File getOptionFile() {
		return optionFile;
	}

	/**
	 * Get the list of desired encryption options, in preference order
	 * @return the list of encryption options supported by the client in preference order
	 */
	public static List<String> getDesiredEncrypts() {
		return desiredEncrypts;
	}

	/**
	 * Get the list of desired generic options
	 * @return the list of desired options supported by the client
	 */
	public static List<String> getDesiredOpts() {
		return desiredOpts;
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
}
