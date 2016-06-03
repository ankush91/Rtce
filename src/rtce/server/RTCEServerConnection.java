package rtce.server;

import rtce.RTCEDocument;
import rtce.RTCEPermission;

/**
 * RTCEServerConnect
 * Stores connection information for the server, including session id.
 * Minimally used in current implementation
 * @author Edwin Dauber, Ankush Israney, Anthony Emma, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 */
public class RTCEServerConnection {

	//The encryption module
	private RTCEServerEncrypt encryptModule;

	//The option modules
	private RTCEServerOpt optionModules[];

	//The document
	private RTCEDocument document;

	//The permission level
	private RTCEPermission permission;

	//The session id
	private long sessionId;

	//The version number
	private byte version[];

	//The port number
	private int portNumber;

	/**
	 * Create the connection
	 * @param encrypt - the name of the encryption technique
	 * @param opts - the names of the options to use
	 */
	public RTCEServerConnection(String encrypt, String[] opts, RTCEDocument doc, RTCEPermission perm, long sid, byte v[]){
		encryptModule = new RTCEServerEncrypt(encrypt);
		if(opts == null){
			optionModules = new RTCEServerOpt[0];
		}else{
			optionModules = new RTCEServerOpt[opts.length];
		}
		for(int i = 0; i < optionModules.length; i++){
			optionModules[i] = new RTCEServerOpt(opts[i]);
		}
		document = doc;
		permission = perm;
		sessionId = sid;
		version = v;
	}

	/**
	 * Get the encryption module
	 * @return the encryption module
	 */
	public RTCEServerEncrypt getEncryptModule() {
		return encryptModule;
	}

	/**
	 * Get the option modules
	 * @return the option module
	 */
	public RTCEServerOpt[] getOptionModules() {
		return optionModules;
	}

	/**
	 * Get the document of the connection
	 * @return the document the connection is working on
	 */
	public RTCEDocument getDocument() {
		return document;
	}

	/**
	 * Get the permission of the user with respect to the document
	 * @return the permission level
	 */
	public RTCEPermission getPermission() {
		return permission;
	}

	/**
	 * Get the session id
	 * @return the session id as a long
	 */
	public long getSessionId() {
		return sessionId;
	}

	/**
	 * Get the version
	 * @return version
	 */
	public byte[] getVersion() {
		return version;
	}

	/**
	 * Get the port number
	 * @return port number
	 */
	public int getPortNumber() {
		return portNumber;
	}


}
