package rtce.server;

import rtce.RTCEDocument;
import rtce.RTCEPermission;

public class RTCEServerConnection {
	
	//The encryption module
	private RTCEServerEncrypt encryptModule;
	
	//The option modules
	private RTCEServerOpt[] optionModules;
	
	//The document
	private RTCEDocument document;
	
	//The permission level
	private RTCEPermission permission;
	
	//The session id
	private long sessionId;
	
	/**
	 * Create the connection
	 * @param encrypt - the name of the encryption technique
	 * @param opts - the names of the options to use
	 */
	public RTCEServerConnection(String encrypt, String[] opts, RTCEDocument doc, RTCEPermission perm, long sid){
		encryptModule = new RTCEServerEncrypt(encrypt);
		optionModules = new RTCEServerOpt[opts.length];
		for(int i = 0; i < opts.length; i++){
			optionModules[i] = new RTCEServerOpt(opts[i]);
		}
		document = doc;
		permission = perm;
		sessionId = sid;
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
	 * @return teh session id as a long
	 */
	public long getSessionId() {
		return sessionId;
	}
}
