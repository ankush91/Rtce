package rtce.server;

public class RTCEServerOpt {
	//The name of the option module
	private String opt;
	
	/**
	 * Create the option module
	 * @param o - the name of the option
	 */
	public RTCEServerOpt(String o){
		opt = o;
	}
	
	/**
	 * Get the secrets to be sent to the client
	 * @return the array of secrets to send to the client
	 */
	public String[] getSecrets(){
		return null;
	}
}
