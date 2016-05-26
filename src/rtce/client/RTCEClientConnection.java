package rtce.client;

import rtce.RTCEDocument;

public class RTCEClientConnection {
	
	//The encryption module
	private RTCEClientEncrypt encryptModule;

	//The option modules
	private RTCEClientOpt[] optionModules;

	//The document
	private RTCEDocument document;

	/**
	 * Create the connection
	 * @param encrypt - the name of the encryption technique
	 * @param opts - the names of the options to use
	 */
	public RTCEClientConnection(String encrypt, String[] opts, RTCEDocument doc){
		encryptModule = new RTCEClientEncrypt(encrypt);
		optionModules = new RTCEClientOpt[opts.length];
		for(int i = 0; i < opts.length; i++){
			optionModules[i] = new RTCEClientOpt(opts[i]);
		}
		document = doc;
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
}
