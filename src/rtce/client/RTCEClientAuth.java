package rtce.client;

import rtce.server.RTCEServerMessage;

public class RTCEClientAuth {

	private String username;
	private String password;
	private String documentOwner;
	private String documentTitle;
	
	private RTCEClientMessage clientMessage;
	private RTCEServerMessage serverMessage;
	
	public RTCEClientAuth(String uname, String pword, String downer, String dtitle){
		username = uname;
		password = pword;
		documentOwner = downer;
		documentTitle = dtitle;
	}
	
}
