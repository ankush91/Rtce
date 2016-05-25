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

import rtce.RTCEDocument;
import rtce.RTCEMessageType;
import rtce.client.RTCEClientMessage;

public class RTCEServerAuth {

	//The client message used to construct the module
	private RTCEClientMessage clientMessage;
	
	//The server message constructed by the module
	private RTCEServerMessage serverMessage;
	
	/**
	 * Create the server authentication module from the CUAUTH message
	 * @param m - a CUAUTH message
	 */
	public RTCEServerAuth(RTCEClientMessage m){
		clientMessage = m;
		if(performAuth()){
			String encrypts[] = new String[1];
			encrypts[0] = chooseEncrypt();
			serverMessage = new ControlMessage();
			serverMessage.setRequest(RTCEMessageType.CONNECT);
			serverMessage.setPassword(RTCEServerConfig.getHostKey());
			serverMessage.setEncryptOpts(encrypts);
			serverMessage.setGenericOpts(chooseOpts());
		}else{
			//TODO send some sort of denial message
		}
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
	public boolean performAuth(){
		Map<String, String> authMap = RTCEServerConfig.getAuthMap();
		if(validAuth()){
			if(authMap.containsKey(clientMessage.getUsername()) && authMap.get(clientMessage.getUsername()).equals(clientMessage.getPassword())){
				return true;
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
	public RTCEClientMessage getClientMessage() {
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
	public String chooseEncrypt(){
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
	 * Select options for the session
	 * @return the chosen options as an array of strings
	 */
	public String[] chooseOpts(){
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
			return null;
		}else{
			String result[] = new String[resultOpts.size()];
			result = resultOpts.toArray(result);
			return result;
		}
	}
	
	/**
	 * Takes the document information from the message to return the document
	 * @return the document itself
	 * @throws IOException - if a document is meant to be created but cannot be.
	 */
	public RTCEDocument openDoc() throws IOException{
		String docOwner = clientMessage.getDocumentOwner();
		String docTitle = clientMessage.getDocumentTitle();
		String docPath = RTCEServerConfig.getDocumentDir().getPath();
		File doc = new File(docPath + "/" + docOwner + "/" + docTitle + RTCEServerConfig.getFileExt());
		if(doc.exists()){
			return new RTCEDocument(doc.getPath());
		}else if(docOwner.equals(clientMessage.getUsername())){
			doc.createNewFile();
			return new RTCEDocument(doc.getPath());
		}else{
			throw new IOException("The document does not exist: " + docOwner + "/" + docTitle);
		}
	}
}
