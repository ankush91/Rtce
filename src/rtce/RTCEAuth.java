package rtce;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

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
	
	
	public boolean performAuth() throws FileNotFoundException{
		if(validAuth()){
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(RTCEServerConfig.getAuthFile()), RTCEServerConfig.getEncoding()));
			
		}else{
			return false;
		}
	}
}
