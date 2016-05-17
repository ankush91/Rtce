package rtce;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class RTCEServerAuth {

	private RTCEMessage clientMessage;
	private RTCEMessage serverMessage;
	
	public RTCEServerAuth(RTCEMessage m){
		clientMessage = m;
	}
	
	private boolean validAuth(){
		if(clientMessage.getRequest() == RTCEMessageType.CUAUTH){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean performAuth(){
		HashMap<String, String> authMap = RTCEServerConfig.getAuthMap();
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

	public RTCEMessage getClientMessage() {
		return clientMessage;
	}

	public RTCEMessage getServerMessage() {
		return serverMessage;
	}
	
	public String chooseEncrypt(){
		String encryptOpts[] = clientMessage.getEncryptOpts();
		ArrayList<String> validEncrypts = RTCEServerConfig.getValidEncrypts();
		for(int i = 0; i < encryptOpts.length; i++){
			if(validEncrypts.contains(encryptOpts[i])){
				return encryptOpts[i];
			}
		}
		return "NONE";
	}
	
	
}
