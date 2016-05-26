package rtce.client;

public class RTCEClientEncrypt {
	
	//The name of the encryption module
	private String method;
	
	/**
	 * Create the encryption module
	 * @param m - the name of the module
	 */
	public RTCEClientEncrypt(String m){
		method = m;
	}
	
	/**
	 * Take the parts of the list of secrets applicable to encryption and return the remainder
	 * @param sec - the set of secrets
	 * @return the remaining secrets
	 */
	public String[] extractSecrets(String[] sec){
		String leftOver[];
		if(method.equals("NONE")){
			leftOver = sec;
		}else{
			leftOver = sec;
		}
		return leftOver;
	}
}
