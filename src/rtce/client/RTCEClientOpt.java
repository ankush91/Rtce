package rtce.client;

/**
 * RTCEClientOpt
 * The client option module
 * Minimally used in current implementation, as we have no options
 * @author Edwin Dauber, Ankush Israney, Anthony Emma, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 */
public class RTCEClientOpt {

	//The name of the option module
	private String opt;

	/**
	 * Create the option module
	 * @param o - the name of the option
	 */
	public RTCEClientOpt(String o){
		opt = o;
	}

	/**
	 * Take the parts of the list of secrets applicable to this option and return the remainder
	 * @param sec - the set of secrets
	 * @return the remaining secrets
	 */
	public String[] extractSecrets(String[] sec){
		return sec;
	}

	/**
	 * Get the option name
	 * @return the option name
	 */
	public String getOpt() {
		return opt;
	}
}
