package rtce;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

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
	
	
	public boolean performAuth() throws IOException{
		if(validAuth()){
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(RTCEServerConfig.getAuthFile()), RTCEServerConfig.getEncoding()));
			String line = reader.readLine();
			while(line != null){
				if(line.trim().equals("Username: " + message.getUsername())){
					line = reader.readLine();
					while(line != null && !line.trim().equals("")){
						if(line.trim().equals("Password: " + message.getPassword())){
							return true;
						}else if(line.trim().startsWith("Password: ")){
							return false;
						}else{
							line = reader.readLine();
						}
					}
				}
			}
			return false;
		}else{
			return false;
		}
	}
}
