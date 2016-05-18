package rtce;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class RTCEMessage {

	private RTCEMessageType request;
	private String username;
	private String password;
	private String encryptOpts[];
	private String genericOpts[];
	private long sessionId;
	private int checksum;
	private int headerReserved1;
	private int headerReserved2;
	private int headerReserved3;

	public RTCEMessageType getRequest() {
		return request;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setRequest(RTCEMessageType request) {
		this.request = request;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getEncryptOpts() {
		return encryptOpts;
	}

	public void setEncryptOpts(String[] encryptOpts) {
		this.encryptOpts = encryptOpts;
	}

	public String[] getGenericOpts() {
		return genericOpts;
	}

	public void setGenericOpts(String[] genericOpts) {
		this.genericOpts = genericOpts;
	}
	
	public void setRequest(byte[] requestChars){
		String requestName = new String(requestChars, RTCEConstants.getRtcecharset());
		request = RTCEMessageType.valueOf(requestName);
	}
	
	public byte[] getRequestChars(){
		byte[] requestName = new byte[RTCEConstants.getRequestlength()];
		byte[] requestBytes = request.toString().getBytes(RTCEConstants.getRtcecharset());
		if(requestBytes.length == RTCEConstants.getRequestlength()){
			requestName = requestBytes;
		}else{
			for(int i = 0; i < requestBytes.length; i++){
				requestName[i] = requestBytes[0];
			}
			for(int i = requestBytes.length; i < requestName.length; i++){
				requestName[i] = 0;
			}
		}
		return requestName;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public int getChecksum() {
		return checksum;
	}

	public void setChecksum(int checksum) {
		this.checksum = checksum;
	}

	public int getHeaderReserved1() {
		return headerReserved1;
	}

	public void setHeaderReserved1(int headerReserved1) {
		this.headerReserved1 = headerReserved1;
	}

	public int getHeaderReserved2() {
		return headerReserved2;
	}

	public void setHeaderReserved2(int headerReserved2) {
		this.headerReserved2 = headerReserved2;
	}

	public int getHeaderReserved3() {
		return headerReserved3;
	}

	public void setHeaderReserved3(int headerReserved3) {
		this.headerReserved3 = headerReserved3;
	}

	// This function clears the Section List stored
	// used only for S_LIST MessageType
    public void clearSectionList() {
      //To be filled in by Anthony
    }
    
    // This function adds a section range to the message.
    // used only for S_LIST MessageType
    public void addToSectionList(int Start, int Finish) {
      //To be filled in by Anthony	
    }
    
    // This function sets the data for the S_DATA MessageType
    public void setSectionData(int SectionID, int SeqID, int CompleteID, String Data)
    {
      //To be filled in by Anthony    	
    }
    
    // This function builds and transmits the Message on the supplied socket
    public void sendMessage(Socket s)
    {
       byte[] buffer = new byte[100000]; //The outgoing buffer, arbitrarily declared to some max length
       int buffer_size = 0; 
    
       //Insert logic to build header data into buffer
       
       //Use the request type to build the remainder of the message
       switch (request) {
       case CUAUTH:
    	  break;
       case CONNECT:
     	  break;    	   
       case S_LIST:
     	  break;    	   
       case S_DATA:
     	  break;    	   
       case S_TREQST:
     	  break;    	   
       case S_TRESPN:
     	  break;    	   
       case S_DENIED:
     	  break;    	   
       case S_DONE:
     	  break;    	   
       case S_COMMIT:
     	  break;    	   
       case ABORT:
     	  break;    	   
       case ECHO:
     	  break;    	   
       case BLOCK:
     	  break;    	   
       case LOGOFF:
     	  break;    	   
       case LACK:
     	  break;    	   
       case CACK:
     	  break;    	   
       case S_REVOKE:
     	  break;    	   
    	        	
       } //switch (request)
       
       //Send the message/buffer
       try{
       s.getOutputStream().write(buffer, 0, buffer_size);
       }
       catch(IOException ex)
       { System.err.println("IOException in sendMessage"); }       
    }
}
