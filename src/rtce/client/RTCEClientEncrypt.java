package rtce.client;

import rtce.RTCECrypt;

public class RTCEClientEncrypt implements RTCECrypt{
	
	//The name of the encryption module
	private String method;
	
	//The underlying cryptographer
	private RTCECrypt crypto;
	
	/**
	 * Create the encryption module
	 * @param m - the name of the module
	 */
	public RTCEClientEncrypt(String m){
		method = m;
		switch(method){
			case "NONE":
				crypto = null;
				break;
			default:
				crypto = null;
				break;
		}
	}
	
	/**
	 * Take the parts of the list of secrets applicable to encryption and return the remainder
	 * @param sec - the set of secrets
	 * @return the remaining secrets
	 */
	public String[] extractSecrets(String[] sec){
		if(crypto == null){
			return sec;
		}else{
			return crypto.extractSecrets(sec);
		}
	}
	
	/**
	 * Return the secrets for the client
	 * As a matter of secrecy, it returns null
	 * If in the future it is necessary to actually return the secrets, this can be changed
	 * @return the secrets (or null, for secrecy's sake)
	 */
	public String[] getSecrets(){
		return null;
	}

	/**
	 * Encrypt the plaintext by passing to the underlying cryptographer
	 * @param the plaintext to encrypt
	 * @return the encrypted ciphertext
	 */
	public byte[] encrypt(byte[] plaintext) {
		if(crypto == null){
			return plaintext;
		}else{
			return crypto.encrypt(plaintext);
		}
	}

	/**
	 * Decrypt the ciphertext by passing to the underlying cryptographer
	 * @param the ciphertext to decrypt
	 * @return the decrypted plaintext
	 */
	public byte[] decrypt(byte[] ciphertext) {
		if(crypto == null){
			return ciphertext;
		}else{
			return crypto.decrypt(ciphertext);
		}
	}
}
