package rtce.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rtce.RTCEConstants;
import rtce.RTCEDocument;
import rtce.RTCEMessageType;
import rtce.RTCEPermission;
import rtce.client.RTCEClientMessage;

public class RTCEServerAuth {

	//The client message used to construct the module
	private RTCEServerMessage clientMessage;
	
	//The server message constructed by the module
	private RTCEServerMessage serverMessage;
	
	private long sessionId;
	
	private RTCEServerConnection connection;
	
	/**
	 * Create the server authentication module from the CUAUTH message
	 * @param m - a CUAUTH message
	 */
	public RTCEServerAuth(RTCEServerMessage m){
		clientMessage = m;
		if(performAuth()){
			try {
				RTCEDocument doc = openDoc();
				RTCEPermission perm;
				if(clientMessage.getUsername().equals(clientMessage.getDocumentOwner())){
					perm = RTCEPermission.OWNER;
				}else{
					perm = RTCEPermission.EDITOR;
				}
				String encrypts[] = new String[1];
				encrypts[0] = chooseEncrypt();
				String opts[] = chooseOpts();
				serverMessage = new RTCEServerMessage();
				serverMessage.setRequest(RTCEMessageType.CONNECT);
				serverMessage.setUsername(RTCEServerConfig.getHostKey());
				serverMessage.setEncryptOpts(encrypts);
				serverMessage.setGenericOpts(opts);
				sessionId = generateSessionId();
				serverMessage.setSessionId(sessionId);
				serverMessage.setVersion(versionMatch(clientMessage.getVersion()));
				connection = new RTCEServerConnection(encrypts[0], opts, doc, perm, sessionId, serverMessage.getVersion());
				String secrets[] = chooseSecrets(connection.getEncryptModule(), connection.getOptionModules());
				serverMessage.setSharedSecrets(secrets);
				
				// TODO add connection to list of current connections
			} catch (IOException e) {
				serverMessage = new RTCEServerMessage();
				serverMessage.setRequest(RTCEMessageType.S_DENIED);
				serverMessage.setSessionId(0);
				// TODO send some sort of denial message because cannot open document
			}
		}else{
			//TODO send some sort of denial message because invalid credentials/message
			serverMessage = new RTCEServerMessage();
			serverMessage.setRequest(RTCEMessageType.S_DENIED);
			serverMessage.setSessionId(0);
		}
	}
	
	/**
	 * Generate the session id for the connection
	 * @return the session id
	 */
	private long generateSessionId(){
		//TODO decide on proper randomization
		Random rand = new Random();
		long value = 0;
		//TODO also check current session ids
		while(value == 0){
			value = rand.nextLong();
			//value = Integer.toUnsignedLong(rand.nextInt());
		}
		return value;
	}
	
	/**
	 * Determine if this is a valid authentication module
	 * @return true if the message is CUAUTH, false otherwise
	 */
	private boolean validAuth(){
		if(clientMessage.getRequest() == RTCEMessageType.CUAUTH){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Determine if the authentication credentials are valid
	 * @return true if valid authentication, false otherwise
	 */
	private boolean performAuth(){
		Map<String, String> authMap = RTCEServerConfig.getAuthMap();
		if(validAuth()){
			if(authMap.containsKey(clientMessage.getUsername())){
				if(authMap.get(clientMessage.getUsername()).equals(clientMessage.getPassword())){
					return true;
				}else{
					return false;
				}
			}else if(clientMessage.getUsername() != null && clientMessage.getPassword() != null && clientMessage.getUsername().trim().length() > 0 && clientMessage.getPassword().trim().length() > 0){
				return RTCEServerConfig.createAuthor(clientMessage.getUsername(), clientMessage.getPassword());
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	/**
	 * Get the client message used to construct the module
	 * @return the message which built the module
	 */
	public RTCEServerMessage getClientMessage() {
		return clientMessage;
	}

	/**
	 * Get the server message constructed by the module
	 * @return the message built by the module
	 */
	public RTCEServerMessage getServerMessage() {
		return serverMessage;
	}
	
	/**
	 * Choose the encryption method for the session
	 * @return the encryption option as a string
	 */
	private String chooseEncrypt(){
		String encryptOpts[] = clientMessage.getEncryptOpts();
		List<String> validEncrypts = RTCEServerConfig.getValidEncrypts();
		if(validEncrypts.size() == 0){
			return "NONE";
		}
		for(int i = 0; i < encryptOpts.length; i++){
			if(validEncrypts.contains(encryptOpts[i])){
				return encryptOpts[i];
			}
		}
		return "NONE";
	}
	
	/**
	 * Choose shared secrets for the client
	 * @return the shared secrets to send to the client
	 */
	private String[] chooseSecrets(RTCEServerEncrypt encrypt, RTCEServerOpt[] opts){
		String secrets[];
		String encryptSecrets[] = encrypt.getSecrets();
		ArrayList<String[]> optSecrets = new ArrayList<String[]>();
		for(RTCEServerOpt opt : opts){
			optSecrets.add(opt.getSecrets());
		}
		int numSecrets = encryptSecrets.length;
		for(String[] os : optSecrets){
			numSecrets += os.length;
		}
		secrets = new String[numSecrets];
		int i = 0;
		for(; i < encryptSecrets.length; i++){
			secrets[i] = encryptSecrets[i];
		}
		for(String[] os : optSecrets){
			for(int j = 0; j < os.length; j++){
				secrets[i+j] = os[j];
			}
			i += os.length;
		}
		return secrets;
	}
	
	/**
	 * Select options for the session
	 * @return the chosen options as an array of strings
	 */
	private String[] chooseOpts(){
		String opts[] = clientMessage.getGenericOpts();
		List<String> validOpts = RTCEServerConfig.getValidOpts();
		ArrayList<String> resultOpts = new ArrayList<String>();
		if(validOpts.size() == 0){
			return null;
		}
		for(int i = 0; i < opts.length; i++){
			if(validOpts.contains(opts[i])){
				resultOpts.add(opts[i]);
			}
		}
		if(resultOpts.size() == 0){
			return new String[0];
		}else{
			String result[] = new String[resultOpts.size()];
			result = resultOpts.toArray(result);
			return result;
		}
	}
	
	/**
	 * Determine if the user is allowed to edit the document
	 * @return true if user permitted, false otherwise
	 * @throws IOException - if cannot read the permission file
	 */
	public boolean hasPermissions() throws IOException{
		String docOwner = clientMessage.getDocumentOwner();
		String username = clientMessage.getUsername();
		if(docOwner.equals(username)){
			return true;
		}
		String docTitle = clientMessage.getDocumentTitle();
		String docPath = RTCEServerConfig.getDocumentDir().getPath();
		File permDoc = new File(docPath + "/" + docOwner + "/" + RTCEServerConfig.getPermissions());
		if(permDoc.exists()){
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(permDoc), RTCEConstants.getRtcecharset()));
			String line = reader.readLine();
			while(line != null){
				line = line.trim();
				if(line.equals(docTitle + RTCEServerConfig.getFileExt() + " PermissionsMetadata{")){
					line = reader.readLine();
					while(!line.equals("} " + docTitle + RTCEServerConfig.getFileExt() + " PermissionsMetadata")){
						line = line.trim();
						if(line.equals(username)){
							return true;
						}
						line = reader.readLine();
					}
					break;
				}
				line = reader.readLine();
			}
			return false;
		}else{
			return false;
		}
	}
	
	/**
	 * Takes the document information from the message to return the document
	 * @return the document itself
	 * @throws IOException - if a document is meant to be created but cannot be.
	 */
	private RTCEDocument openDoc() throws IOException{
		String docOwner = clientMessage.getDocumentOwner();
		String docTitle = clientMessage.getDocumentTitle();
		String username = clientMessage.getUsername();
		String docPath = RTCEServerConfig.getDocumentDir().getPath();
		File doc = new File(docPath + "/" + docOwner + "/" + docTitle + RTCEServerConfig.getFileExt());
		if(doc.exists() && hasPermissions()){
			return new RTCEDocument(docPath, docOwner, docTitle, RTCEServerConfig.getFileExt());
		}else if(docOwner.equals(username)){
			doc.createNewFile();
			return new RTCEDocument(docPath, docOwner, docTitle, RTCEServerConfig.getFileExt());
		}else{
			throw new IOException("The document does not exist: " + docOwner + "/" + docTitle);
		}
	}
	
	/**
	 * Match server version to client version, and return the lowest supported version
	 * @param cVersion - the client version
	 * @return the lowest supported version
	 */
	private byte[] versionMatch(byte[] cVersion){
		byte sVersion[] = RTCEServerConfig.getVersion();
		byte result[] = new byte[4];
		for(int i = 0; i < 4; i++){
			if(sVersion[i] <= cVersion[i]){
				result[i] = sVersion[i];
			}else{
				result[i] = cVersion[i];
			}
		}
		return result;
	}

	public long getSessionId() {
		return sessionId;
	}

	public RTCEServerConnection getConnection() {
		return connection;
	}
	
	
}
