package rtce.client;

import java.util.List;

import rtce.RTCEMessageType;

/**
 * RTCEClientAuth
 * The client authentication module for the initial handshake
 * @author Edwin Dauber, Ankush Israney, Anthony Emma, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 */
public class RTCEClientAuth {

	//Authentication information
	private String username;
	private String password;
	private String documentOwner;
	private String documentTitle;
	
	//involved messages
	private RTCEClientMessage clientMessage;
	private RTCEClientMessage serverMessage;
	private RTCEClientMessage cack;
	
	/**
	 * Create the authentication module from user input
	 * @param uname - the username
	 * @param pword - the password
	 * @param downer - the document owner
	 * @param dtitle - the document title
	 */
	public RTCEClientAuth(String uname, String pword, String downer, String dtitle){
		username = uname;
		password = pword;
		documentOwner = downer;
		documentTitle = dtitle;
		buildMessage();
	}

	/**
	 * Build the client authentication message
	 */
	private void buildMessage(){
		clientMessage = new RTCEClientMessage();
		clientMessage.setRequest(RTCEMessageType.CUAUTH);
		clientMessage.setDocumentOwner(documentOwner);
		clientMessage.setDocumentTitle(documentTitle);
		clientMessage.setPassword(password);
		clientMessage.setUsername(username);
		clientMessage.setEncryptOpts(getEncryptOpts());
		clientMessage.setGenericOpts(getGenericOpts());
		clientMessage.setVersion(RTCEClientConfig.getVersion());
		clientMessage.setSessionId(0);
	}
	
	/**
	 * Get the client authentication message
	 * @return the client authentication message
	 */
	public RTCEClientMessage getClientMessage() {
		return clientMessage;
	}

	/**
	 * Get the server connection response
	 * @return the server connection response
	 */
	public RTCEClientMessage getServerMessage() {
		return serverMessage;
	}
	
	/**
	 * Get the client acknowledgement message
	 * @return the client acknowledgement message
	 */
	public RTCEClientMessage getCack() {
		return cack;
	}
	
	/**
	 * Set the server connection response
	 * @param serverMessage = the server connection response
	 */
	public void setServerMessage(RTCEClientMessage serverMessage) {
		this.serverMessage = serverMessage;
	}

	/**
	 * Get the ordered array of desired encryption options
	 * @return an ordered string array
	 */
	private String[] getEncryptOpts(){
		List<String> encrypt = RTCEClientConfig.getDesiredEncrypts();
		return encrypt.toArray(new String[encrypt.size()]);
	}
	
	/**
	 * Get the array of desired generic options
	 * @return a string array
	 */
	private String[] getGenericOpts(){
		List<String> opts = RTCEClientConfig.getDesiredOpts();
		return opts.toArray(new String[opts.size()]);
	}
	
	/**
	 * Generate and return the client connection object
	 * @return the client connection object
	 */
	public RTCEClientConnection getConnection(){
		String encrypt = serverMessage.getEncryptOpts()[0];
		String opts[] = serverMessage.getGenericOpts();
		String sec[] = serverMessage.getSharedSecrets();
		long session = serverMessage.getSessionId();
		byte version[] = serverMessage.getVersion();
		cack = new RTCEClientMessage();
		cack.setSessionId(session);
		cack.setRequest(RTCEMessageType.CACK);
		return new RTCEClientConnection(encrypt, opts, sec, null, session, version);
	}
}
