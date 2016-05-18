package rtce;

import java.io.IOException;
import java.net.*;

public class RTCEMessage {

	//The request type
	private RTCEMessageType request;
	
	//The username, for appropriate messages
	private String username;
	
	//The password, for appropriate messages
	private String password;
	
	//The encryption options, for appropriate messages
	private String encryptOpts[];
	
	//The other option list, for appropriate messages
	private String genericOpts[];
	
	//The session ID for all headers
	private long sessionId;
	
	//The checksum, for all headers
	private int checksum;
	
	//The reserved fields, for all headers
	private int headerReserved1;
	private int headerReserved2;
	private int headerReserved3;

	/**
	 * Get the message type
	 * @return The message type
	 */
	public RTCEMessageType getRequest() {
		return request;
	}

	/**
	 * Get the username as a string, if applicable
	 * @return The username as a string
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get the password as a string, if applicable
	 * @return The password as a string
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set the request type
	 * @param request - the request type
	 */
	public void setRequest(RTCEMessageType request) {
		this.request = request;
	}

	/**
	 * Set the username
	 * @param username as a string
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Set the password
	 * @param password as a string
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get the encryption options
	 * @return the array of encryption options as strings
	 */
	public String[] getEncryptOpts() {
		return encryptOpts;
	}

	/**
	 * Set the encryption options
	 * @param encryptOpts as an array of strings
	 */
	public void setEncryptOpts(String[] encryptOpts) {
		this.encryptOpts = encryptOpts;
	}

	/**
	 * Get the generic options
	 * @return the array of generic options as strings
	 */
	public String[] getGenericOpts() {
		return genericOpts;
	}

	/**
	 * Set the generic options
	 * @param genericOpts as an array of strings
	 */
	public void setGenericOpts(String[] genericOpts) {
		this.genericOpts = genericOpts;
	}
	
	/**
	 * Set the request as a byte array
	 * @param requestChars as a byte array
	 */
	public void setRequest(byte[] requestChars){
		String requestName = new String(requestChars, RTCEConstants.getRtcecharset());
		request = RTCEMessageType.valueOf(requestName);
	}
	
	/**
	 * Get the request as a byte array
	 * @return byte array representing the request
	 */
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

	/**
	 * Get the session id
	 * @return the session id as a long
	 */
	public long getSessionId() {
		return sessionId;
	}

	/**
	 * Set the session id
	 * @param sessionId as a long
	 */
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Get the checksum
	 * @return checksum as an integer
	 */
	public int getChecksum() {
		return checksum;
	}

	/**
	 * Set the checksum
	 * @param checksum an in integer
	 */
	public void setChecksum(int checksum) {
		this.checksum = checksum;
	}

	/**
	 * Get the header reserved field 1
	 * @return header reserved field 1 as an integer
	 */
	public int getHeaderReserved1() {
		return headerReserved1;
	}

	/**
	 * Set the header reserved field 1
	 * @param headerReserved1 as an integer
	 */
	public void setHeaderReserved1(int headerReserved1) {
		this.headerReserved1 = headerReserved1;
	}

	/**
	 * Get the header reserved field 2
	 * @return header reserved field 2 as an integer
	 */
	public int getHeaderReserved2() {
		return headerReserved2;
	}

	/**
	 * Set the header reserved field 2
	 * @param headerReserved2 as an integer
	 */
	public void setHeaderReserved2(int headerReserved2) {
		this.headerReserved2 = headerReserved2;
	}

	/**
	 * Get the header reserved field 3
	 * @return header reserved field 3 as an integer
	 */
	public int getHeaderReserved3() {
		return headerReserved3;
	}

	/**
	 * Set the header reserved field 3
	 * @param headerReserved3 as an integer
	 */
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
