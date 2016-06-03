package rtce.server;

/**
 * RTCEClientOpt
 * The client option module
 * Minimally used in current implementation, as we have no options
 * @author Edwin Dauber, Ankush Israney, Anthony Emma, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 */
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

	/**
	 * Get the option name
	 * @return the option name
	 */
	public String getOpt() {
		return opt;
	}

}
