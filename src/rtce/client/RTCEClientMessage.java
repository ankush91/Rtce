package rtce.client;

import rtce.RTCEMessageType;
import rtce.RTCEConstants;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;


public class RTCEClientMessage {

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
	
        //The time stamp value
        private long timeStamp;
        
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
		byte[] requestName = new byte[RTCEConstants.getRequestLength()];
		byte[] requestBytes = request.toString().getBytes(RTCEConstants.getRtcecharset());
		if(requestBytes.length == RTCEConstants.getRequestLength()){
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

        //We will tackle exact issue of timing when testing the control flow
        public void setTime()
        {
            this.timeStamp = System.nanoTime();
        }
        
        public long getTime()
        {
            return timeStamp;
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
        
    public ByteBuffer setHeader()
    {
        ByteBuffer bbuf = ByteBuffer.allocate(40);
        bbuf.put(getRequestChars());
        bbuf.putLong(23);
        bbuf.putLong(getTime());
        bbuf.putInt(1001);
        bbuf.putInt(1);
        bbuf.putInt(2);
        bbuf.putInt(3);
        return bbuf;
        
    }       
   
    // This function builds and transmits the Message on the supplied socket
    public void sendMessage(Socket s, RTCEMessageType option)
    {
        //header information in messageheader
        ByteBuffer messageHeader = setHeader();
        
        //Initialization of Data and Control Message class objects
        
        DataMessage dataPayload = null;
        
        ControlMessage controlPayload = null;
        
       //Use the request type to build the message
       //CLIENT CAN SEND ONLY SPECIFIC MESSAGES, option with request for now
       switch (option) 
       {
       case CUAUTH:
          //payload = setCUAUTH(); 
    	  break;
       case CONNECT:
           //payload = setCONNECT();
     	  break;    	   
       case S_LIST:
           //payload = setS_LIST();
     	  break;    	   
       case S_DATA:
           //payload = setS_DATA();
     	  break;  
          
        // S_TREQST for testing
       case S_TREQST:
            controlPayload = new ControlMessage(12);
            controlPayload.payload = controlPayload.setSTREQST();
           
     	  break;    
          
       case S_TRESPN:
           //payload = setS_TRESPN();
     	  break;    	   
       case S_DENIED:
           //payload = setS_DENIED();
     	  break;    	   
       case S_DONE:
           //payload = setS_DONE();
     	  break;    	   
       case S_COMMIT:
           //payload = setS_COMMIT();
     	  break;    	   
       case ABORT:
           //payload = setS_ABORT();
     	  break;    	   
       case ECHO:
           //payload = setECHO();
     	  break;    	   
       case BLOCK:
           //payload = setBLOCK();
     	  break;    	   
       case LOGOFF:
           //payload = setLOGOFF();
     	  break;    	   
       case LACK:
           //payload = setLACK();
     	  break;    	   
       case CACK:
           //payload = setCACK();
     	  break;    	   
       case S_REVOKE:
           //payload = setS_REVOKE();
     	  break;    	   
    	        	
       } //switch (request)
       
       //Message is transmitted over here
        ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                out.write(messageHeader.array());
                out.write(controlPayload.payload.array());
                int a = out.size();
                System.out.println(a);
                s.getOutputStream().write(out.toByteArray(), 0, out.size());
                }
       catch(IOException ex)
       { System.err.println("IOException in sendMessage"); }       
       }   
  }


//Seperate Class for Delineation needed else difficult to debug
class DataMessage extends RTCEClientMessage
{

    // This function clears the Section List stored
    // used only for S_LIST MessageType
    
    public void clearSectionList() 
    {
      //To be filled in by Anthony
    }
    
    // This function adds a section range to the message.
    // used only for S_LIST MessageType
    public void addToSectionList(int Start, int Finish) 
    {
      //To be filled in by Anthony	
    }
    
    // This function sets the data for the S_DATA MessageType
    public void setSectionData(int SectionID, int SeqID, int CompleteID, String Data)
    {
      //To be filled in by Anthony    	
    }
    

}

//Control Messages and everything Else, inheerits both classes | Testing for now
class ControlMessage extends DataMessage
{
   
   ByteBuffer payload;
   int messagetype;
   
   ControlMessage(int byteSize)
   {
       payload = ByteBuffer.allocate(byteSize);
   }
   
     public ByteBuffer setSTREQST() 
    {
         //arbitray for now
        int sectionId = 100;
        int length = 12;
        byte[] options = new byte[4];
        
        //set U and V bit for now
        options[0] = (byte) (options[0] | (1 << 0) | (1 << 1));
        
        ByteBuffer b = ByteBuffer.allocate(12);
        b.putInt(sectionId);
        b.putInt(length);
        b.put(options);
        return b;
        
    }

}