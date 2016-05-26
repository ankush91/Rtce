package rtce.client;

import rtce.RTCEDocument;

public class RTCEClientConnection {
	
	//The encryption module
	private RTCEClientEncrypt encryptModule;

	//The option modules
	private RTCEClientOpt[] optionModules;

	//The document
	private RTCEDocument document;
	
	//The session ID
	private long sessionId;

	/**
	 * Create the connection
	 * @param encrypt - the name of the encryption technique
	 * @param opts - the names of the options to use
	 */
	public RTCEClientConnection(String encrypt, String[] opts, String[] sec, RTCEDocument doc, long sid){
		encryptModule = new RTCEClientEncrypt(encrypt);
		optionModules = new RTCEClientOpt[opts.length];
		for(int i = 0; i < opts.length; i++){
			optionModules[i] = new RTCEClientOpt(opts[i]);
		}
		document = doc;
		sessionId = sid;
		extractSecrets(sec);
	}

	/**
	 * Get the encryption module
	 * @return the encryption module
	 */
	public RTCEClientEncrypt getEncryptModule() {
		return encryptModule;
	}

	/**
	 * Get the option modules
	 * @return the option module
	 */
	public RTCEClientOpt[] getOptionModules() {
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
	 * Get the session id
	 * @return the session id as a long
	 */
	public long getSessionId() {
		return sessionId;
	}
	
	/**
	 * Extract the secrets to the appropriate modules
	 * @param sec - the set of shared secrets
	 */
	private void extractSecrets(String[] sec){
		String postEncrypt[] = encryptModule.extractSecrets(sec);
		String postOpt[] = postEncrypt;
		for(RTCEClientOpt opt : optionModules){
			postOpt = opt.extractSecrets(postOpt);
		}
	}
}
