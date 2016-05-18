package rtce.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rtce.RTCEMessage;
import rtce.RTCEMessageType;

public class RTCEServerAuth {

	//The client message used to construct the module
	private RTCEMessage clientMessage;
	
	//The server message constructed by the module
	private RTCEMessage serverMessage;
	
	/**
	 * Create the server authentication module from the CUAUTH message
	 * @param m - a CUAUTH message
	 */
	public RTCEServerAuth(RTCEMessage m){
		clientMessage = m;
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
	public RTCEMessage getClientMessage() {
		return clientMessage;
	}

	/**
	 * Get the server message constructed by the module
	 * @return the message built by the module
	 */
	public RTCEMessage getServerMessage() {
		return serverMessage;
	}
	
	/**
	 * Choose the encryption method for the session
	 * @return the encryption option as a string
	 */
	public String chooseEncrypt(){
		String encryptOpts[] = clientMessage.getEncryptOpts();
		List<String> validEncrypts = RTCEServerConfig.getValidEncrypts();
		for(int i = 0; i < encryptOpts.length; i++){
			if(validEncrypts.contains(encryptOpts[i])){
				return encryptOpts[i];
			}
		}
		return "NONE";
	}
	
	
}
