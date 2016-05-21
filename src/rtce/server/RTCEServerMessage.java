package rtce.server;

import rtce.RTCEMessageType;
import rtce.RTCEDocument;
import rtce.RTCEConstants;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import rtce.RTCEMessageType;

public class RTCEServerMessage {

	//The request type
	private RTCEMessageType request;
	private RTCEMessageType response;
        
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

	public RTCEDocument document;
	
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
	
	public void setDocument(RTCEDocument doc) {
		this.document = doc;
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
        ByteBuffer bbuf = ByteBuffer.allocate(39);
        bbuf.put(getRequestChars());
        bbuf.putFloat(getSessionId());
        bbuf.putFloat(getTime());
        bbuf.putInt(getChecksum());
        bbuf.putInt(getHeaderReserved1());
        bbuf.putInt(getHeaderReserved2());
        bbuf.putInt(getHeaderReserved2());
        return bbuf;
        
    }       
   
    // This function builds and transmits the Message on the supplied socket
    public void sendMessage(Socket s)
    {
        ByteBuffer messageHeader = setHeader();
        
        DataMessage dataPayload = new DataMessage();
        
        ControlMessage controlPayload = null;
        
       //Use the request type to send the response, still left to work on this
       this.response = this.request;
       if(this.request == RTCEMessageType.S_TREQST)
        this.response = RTCEMessageType.S_TRESPN;
       
       switch (response) 
       {
       case CUAUTH:
          //payload = setCUAUTH(); 
    	  break;
       case CONNECT:
           //payload = setCONNECT();
     	  break;    	   
       case S_LIST:
    	  controlPayload = new ControlMessage(document);
    	  controlPayload.setS_LIST();           
     	  break;    	   
       case S_DATA:
           //payload = setS_DATA();
     	  break;  
          
        // S_TREQST for testing
       case S_TREQST:
         // ;
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
       
       //encapsulate the header over here
        ByteArrayOutputStream out = new ByteArrayOutputStream();
              try {
                out.write(messageHeader.array());
                out.write(controlPayload.payload);
                s.getOutputStream().write(out.toByteArray(), 0, out.size());
                }
                catch(IOException ex)
                { System.err.println("IOException in sendMessage"); }       
                } 

// This function builds and transmits the Message on the supplied socket
    public void recvMessage(Socket s, RTCEMessageType request, ByteBuffer bf)
    {
        //Extract header contents first, needed to make a function out of this
          this.request = request;
          System.out.println("Request in string format: " + request);
          System.out.println(bf.position());
          
          bf.position(8);
          System.out.println("Session ID:   "+ bf.getLong());
          System.out.println("Time Stamp:   "+ bf.getLong());
          
          System.out.println("Checksum: "+ bf.getInt());
          
          System.out.println("Reserved1:    "+bf.getInt());
          
          System.out.println("Reserved 2:   "+ bf.getInt());
         
          System.out.println("Reserved 3:   "+ bf.getInt());
          
         
     
       switch (request) 
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
           
           
           System.out.println("Section ID:" + bf.getInt());
           System.out.println("Length:" + bf.getInt());
           System.out.println("Options: "+ bf.get());
         // ;
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
       
       //encapsulate the header over here
        ByteArrayOutputStream out = new ByteArrayOutputStream();
              try {
                //out.write(messageHeader.array());
                //out.write(controlPayload.payload);
                s.getOutputStream().write(out.toByteArray(), 0, out.size());
                }
                catch(IOException ex)
                { System.err.println("IOException in sendMessage"); }       
    } 
                
 }   

//Seperate Class for Delineation needed else difficult to debug
class DataMessage extends RTCEServerMessage
{

    // This function clears the Section List stored
    // used only for S_LIST MessageType
    
    public void clearSectionList() 
    {
      //To be filled in by Anthony
    }
    
    // This function adds a section range to the message.
    // used only for S_LIST MessageType
    public void addToSectionList(int SectionID) 
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
   
   byte[] payload;
   int messagetype;

   
   ControlMessage(int byteSize)
   {	   
       payload = new byte[byteSize];
   }
   ControlMessage(RTCEDocument doc)
   { document = doc; 
   }
   
   public void setS_LIST()
   {	   
	  int numberOfIDs = this.document.resetSectionItr();	  
	  ByteBuffer bbuf = ByteBuffer.allocate(numberOfIDs*4);
	  	  
	  for(int i = 0; i < numberOfIDs; i++)
	  { bbuf.putInt(document.getNextSectionItr().ID);
      }
      
	  payload = new byte[numberOfIDs*4];
	  payload = bbuf.array();
	   
   }
    
     
    /*public ByteBuffer setS_TRSPN()
    {
        
    }*/
    

}