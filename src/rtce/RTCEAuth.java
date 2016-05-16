package rtce;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class RTCEAuth {

	private RTCEMessage message;
	
	public RTCEAuth(RTCEMessage m){
		message = m;
	}
	
	private boolean validAuth(){
		if(message.getRequest() == RTCEMessageType.CUAUTH){
			return true;
		}else{
			return false;
		}
	}
	
	
	public boolean performAuth(){
		if(validAuth()){
			if(RTCEServerConfig.getAuthMap().containsKey(message.getUsername()) && RTCEServerConfig.getAuthMap().get(message.getUsername()).equals(message.getPassword())){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
}
