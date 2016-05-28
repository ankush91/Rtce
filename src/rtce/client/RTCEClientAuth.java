package rtce.client;

import java.util.List;

import rtce.RTCEMessageType;
import rtce.server.RTCEServerMessage;

public class RTCEClientAuth {

	private String username;
	private String password;
	private String documentOwner;
	private String documentTitle;
	
	private RTCEClientMessage clientMessage;
	private RTCEServerMessage serverMessage;
	
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
		clientMessage = new ControlMessage();
		clientMessage.setRequest(RTCEMessageType.CUAUTH);
		clientMessage.setDocumentOwner(documentOwner);
		clientMessage.setDocumentTitle(documentTitle);
		clientMessage.setPassword(password);
		clientMessage.setUsername(username);
		clientMessage.setEncryptOpts(getEncryptOpts());
		clientMessage.setGenericOpts(getGenericOpts());
		clientMessage.setVersion(RTCEClientConfig.getVersion());
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
	public RTCEServerMessage getServerMessage() {
		return serverMessage;
	}
	
	/**
	 * Set the server connection response
	 * @param serverMessage = the server connection response
	 */
	public void setServerMessage(RTCEServerMessage serverMessage) {
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
		return new RTCEClientConnection(encrypt, opts, sec, null, session, version);
	}
}
