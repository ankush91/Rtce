package rtce.server;

public class RTCEServerEncrypt {
	
	//The name of the encryption module
	private String method;
	
	//The server key, if asymmetric
	private String serverKey;
	
	//The shared key, if symmetric
	private String sharedKey;
	
	/**
	 * Create the encryption module
	 * @param m - the name of the module
	 */
	public RTCEServerEncrypt(String m){
		method = m;
	}
	
	/**
	 * Get the secrets to be sent to the client
	 * @return the array of secrets to send to the client
	 */
	public String[] getSecrets(){
		if(method.equals("NONE")){
			return new String[0];
		}
		return null;
	}
}
