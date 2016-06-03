package rtce.server;

import rtce.RTCECrypt;

/**
 * RTCEServerEncrypt
 * The server encryption module
 * Minimally used in current implementation, as we have only the "NONE" encrypter
 * @author Edwin Dauber, Ankush Israney, Anthony Emma, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 */
public class RTCEServerEncrypt implements RTCECrypt{

	//The name of the encryption module
	private String method;

	//The underlying cryptographer
	private RTCECrypt crypto;

	/**
	 * Create the encryption module
	 * @param m - the name of the module
	 */
	public RTCEServerEncrypt(String m){
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
	 * Get the secrets to be sent to the client
	 * @return the array of secrets to send to the client
	 */
	public String[] getSecrets(){
		if(crypto == null){
			return new String[0];
		}else{
			return crypto.getSecrets();
		}
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

	/**
	 * Extract secrets from passed array
	 * Currently not used
	 * @param the list of secrets
	 * @return unused secrets
	 */
	public String[] extractSecrets(String[] allSecrets) {
		if(crypto == null){
			return allSecrets;
		}else{
			return crypto.extractSecrets(allSecrets);
		}
	}
}
