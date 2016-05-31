package rtce;

public interface RTCECrypt {

	/**
	 * Encrypt using whatever cryptographic algorithm
	 * @param plaintext - the plaintext to encrypt
	 * @return ciphertext - the encrypted ciphertext
	 */
	public byte[] encrypt(byte[] plaintext);
	
	/**
	 * Decrypt using whatever cryptographic algorithm
	 * @param ciphertext - the ciphertext to decrypt
	 * @return plaintext - the decrypted plaintext
	 */
	public byte[] decrypt(byte[] ciphertext);
	
	/**
	 * Extract relevant secrets from an array of secrets
	 * @param allSecrets - the array of all secrets
	 * @return leftovers - the secrets not relevant to the cryptography algorithm
	 */
	public String[] extractSecrets(String[] allSecrets);
	
	/**
	 * Return the secrets for this cryptographer
	 * @return an array of secrets
	 */
	public String[] getSecrets();
}
