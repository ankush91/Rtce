package rtce.client;

import rtce.RTCEMessageType;
import rtce.RTCEConstants;
import rtce.RTCEDocument;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;


public class RTCEClientMessage {

	//The request type
	private RTCEMessageType request, serverResponse;
	
	//The username, for appropriate messages
	private String username;
	
	//The password, for appropriate messages
	private String password;
	
	//The encryption options, for appropriate messages
	private String encryptOpts[];
	
	//The other option list, for appropriate messages
	private String genericOpts[];

	//The identifiers for the the document to access.
	private String documentOwner;
	private String documentTitle;
	
	//The document itself
	public RTCEDocument document;
	
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

	//The three pieces of data for a Commit
	private int    Commit_sID;
	private int    Commit_prevID;
	private String Commit_txt;
	private int    Commit_token;
	
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
	
	public RTCEMessageType getServerResponse() {
		return serverResponse;
	}

	public void setServerResponse(RTCEMessageType serverResponse) {
		this.serverResponse = serverResponse;
	}

	public String getDocumentOwner() {
		return documentOwner;
	}

	public void setDocumentOwner(String documentOwner) {
		this.documentOwner = documentOwner;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public RTCEDocument getDocument() {
		return document;
	}

	/**
	 * Set the documentOwner as a byte array
	 * @param usernameChars as a byte array
	 */
	public void setDocumentOwner(byte[] usernameChars){
		documentOwner = new String(usernameChars, RTCEConstants.getRtcecharset());
	}
	
	/**
	 * Get the documentOwner as a byte array
	 * @return byte array representing the documentOwner
	 */
	public byte[] getDocumentOwnerChars(){
		return RTCEConstants.getStringAsBytes(documentOwner, RTCEConstants.getUsernameLength());
	}
	
	/**
	 * Set the document title as a byte array
	 * @param doucumentTitleChars as a byte array
	 */
	public void setDocumentTitle(byte[] documentTitleChars){
		documentTitle = new String(documentTitleChars, RTCEConstants.getRtcecharset());
	}
	
	/**
	 * Get the document title as a byte array
	 * @return byte array representing the document title
	 */
	public byte[] getDocumentTitleChars(){
		return RTCEConstants.getStringAsBytes(documentTitle, RTCEConstants.getDocTitleLength());
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
                
			for(int i = 0; i < requestBytes.length; i++)
                        {
				requestName[i] = requestBytes[i];
			}
			for(int i = requestBytes.length; i < requestName.length; i++)
                        {
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
        
    public ByteBuffer setHeader(RTCEMessageType request)
    {

        setRequest(request);
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
   
     public void getHeader(ByteBuffer bf)
       {
          String request = new String(bf.array(), 0, 8);
          
          System.out.println("Request in string format: " + request);
          System.out.println(bf.position());
          
          bf.position(8);
          System.out.println("Session ID:   "+ bf.getLong());
          System.out.println("Time Stamp:   "+ bf.getLong());
          
          System.out.println("Checksum: "+ bf.getInt());
          System.out.println("Reserved1:  "+bf.getInt());
          System.out.println("Reserved 2: "+ bf.getInt());
          System.out.println("Reserved 3: "+ bf.getInt());
          
       }  
    
    //This function sets all the data needed to send a S_COMMIT message
    public void setCommitData(int token, int prevID, int sID, String newText)
    {
      Commit_token  = token;
      Commit_prevID = prevID; 
      Commit_sID    = sID;
      Commit_txt    = newText;
    }
    
    // This function builds and transmits the Message on the supplied socket
    public void sendMessage(Socket s, RTCEMessageType option)
    {
        boolean validflag = true;
        
        //header information in messageheader
        ByteBuffer messageHeader = setHeader(option);
        
        //Initialization of Data and Control Message class objects
        
        DataMessage dataPayload = null;
        ControlMessage controlPayload = null;
        
       //Use the request type to build the message
       //CLIENT CAN SEND ONLY SPECIFIC MESSAGES, option with request for now
       switch (option) 
       {
      case CUAUTH:
          controlPayload = new ControlMessage(89);
          controlPayload.payload = controlPayload.setSTREQST();
    	  break;
     
        // S_TREQST for testing
       case S_TREQST:
         
            controlPayload = new ControlMessage(12);
            controlPayload.payload = controlPayload.setSTREQST();
     	  break;    
          
       case S_DONE:
       
           controlPayload = new ControlMessage(8);
            controlPayload.payload = controlPayload.setS_DONE();
           //payload = setS_DONE();
     	  break;    
          
        case S_COMMIT:
            controlPayload = new ControlMessage(4+4+4+4+Commit_txt.length());
            controlPayload.payload.putInt(Commit_token);
            controlPayload.payload.putInt(Commit_prevID);            
            controlPayload.payload.putInt(Commit_sID);
            controlPayload.payload.putInt(Commit_txt.length());
            controlPayload.payload.put(Commit_txt.getBytes());
         	   
       case ABORT: {}
           //payload = setS_ABORT();
     	  break;    	   
       case ECHO: {}
           //payload = setECHO();
     	  break;    	   
      
       case LOGOFF: {}
           //payload = setLOGOFF();
     	  break;    	   
        	   
       case CACK:  {}
           //payload = setCACK();
     	  break;    	   
        
       default: validflag = false;
    	        System.out.println("2");  	
       } //switch (request)
       
       //Message is transmitted over here
       if(validflag)
       {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    out.write(messageHeader.array());
                    
                        if(controlPayload!=null)
                        out.write(controlPayload.payload.array());
                        
                    int a = out.size();
                    System.out.println(a);
                    s.getOutputStream().write(out.toByteArray(), 0, out.size());
                    
                    byte[] test = out.toByteArray();
                     String s1 = new String(test, 0, 8);
                     System.out.println("OUTGOING REQUEST:  " + s1 + "\n");
                    
                    }
                catch(IOException ex) 
                { System.err.println("IOException in sendMessage"); }       
       }   
    }   

    
    
    public int lengthBuffer(String s)
     {
         
            //Allocate buffer length according to request
          switch (s) 
          {
         case "CONNECT":
          return 97;
       
         case "S_LIST":
              return 1000;
              
         case "S_DATA":
              return 1000;
              
          case "S_TRESPN":
          return 72;  
          	   
          case "S_REVOKE":
           return 48;
           
          case "ABORT":
              return 40;
              
          case "S_DENIED":
           return 48;    
          
           case "BLOCK":
           return 64;
          
           case "ECHO":
            return 40;   	
          
           case "LACK":
            return 40;
            
         default: return 0;	        
          
        }
     }
     public int lastByte(byte[] asciiVal)
     {
        for(int i=0;i<asciiVal.length;i++)
            {
                int current = new Byte(asciiVal[i]).intValue();
                if(current == 0)
                {
                     return i;
                }
            }
        return 8;
     }   
     
// This function builds and transmits the Message on the supplied socket
    public void recvMessage(Socket s, RTCEMessageType request, ByteBuffer bf)
    {
        //Extract header contents first, needed to make a function out of this
        getHeader(bf);
        ControlMessage control = new ControlMessage();
        
       switch (request) 
       {
          case CONNECT:
          control.getCONNECT(bf);
    	  break;
           
          case S_LIST:
              System.out.println("S_LIST \n");   
              
              bf.position(40);
              document.clearOrder();
              while (true)
              {
            	int temp = bf.getInt();            	
            	document.setOrder(temp);
            	System.out.print(""+temp+",");
            	if (temp == 0)
            	{System.out.println("");break;}              
              }
    	  break;
           
          case S_DATA:
        	  System.out.println("S_DATA \n");
        	  
        	  bf.position(40);
        	  int sID = bf.getInt();
        	  int txtLen = bf.getInt();
              String sectionTxt = new String(bf.array(), 48, txtLen);
        	  System.out.println("sID="+sID+" txtLen="+txtLen+ "txt=" + sectionTxt);
        	  document.updateSection(sID, sectionTxt);
    	  break;         
       
          case S_TRESPN:
          control.getS_TRESPN(bf);
     	  break;     
          
          case S_DENIED:
           control.getS_DENIED(bf);
            {}
     	  break;    
          
          case S_REVOKE:
           control.getS_REVOKE(bf);
            {}
     	  break;    
          
          case BLOCK:
              control.getBLOCK(bf);
          break;
          
         case ABORT:
           System.out.println("ABORT \n");
             {}
     	  break; 
          
       case ECHO:
           System.out.println("ECHO \n");
            {}
     	  break;    	
          
       case LACK:
            System.out.println("LACK \n");
             {}
     	  break;  

       default: {}
    	        	
       } //switch (request)
            
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
   
   ControlMessage(){}
   
   ControlMessage(int byteSize)
   {
       payload = ByteBuffer.allocate(byteSize);
   }
   
   /*ALL SENDING CONTROL MESSAGES*/
   
    public ByteBuffer setCUAUTH()
    {
       
        byte[] version = new byte[4];
        
        for(int i=0; i<4; i++)
               version[i] = (byte)0xe0;
        
        byte[] username = new byte[20];
        
            for(int i=0; i<20; i++)
               username[i] = (byte)0xe0;
            
        byte[] authentication = new byte[16];
        
            for(int i=0; i<16; i++)
               authentication[i] = (byte)0xe0;
            
        byte[] document_owner = new byte[20];
        
            for(int i=0; i<20; i++)
               document_owner[i] = (byte)0xe0;
             
        byte[] document_title = new byte[20];
                
             for(int i=0; i<20; i++)
               document_title[i] = (byte)0xe0;
             
        int num_encrypt_options = 1;
        
        byte[] option_list = new byte[num_encrypt_options];
        
            for(int i=0; i<num_encrypt_options; i++)
               option_list[i] = (byte)0xe0;
            
        int num_other_options = 1;
        
        byte[] other_option_list = new byte[num_other_options];
        
            for(int i=0; i< num_other_options; i++)
               other_option_list[i] = (byte)0xe0;
            
        ByteBuffer bf = ByteBuffer.allocate(90);
        
        return bf;
        
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
     
       public ByteBuffer setS_DONE()
       {
           //status code 1 means no error
           int statuscode = 1;
           int error = 0;
            ByteBuffer b = ByteBuffer.allocate(8);
            b.putInt(statuscode);
            b.putInt(error);
            return b;
       }
       
       //ALL RECEIVED MESSAGES FROM SERVER SIDE
       public void getS_TRESPN(ByteBuffer bf)
    {   
           bf.position(40);
           System.out.println("token processing..");
           bf.position(56);
           System.out.println("additional priviledges..");
          
    }
       
      public void getS_DENIED(ByteBuffer bf)
    {   
           bf.position(40);
           System.out.println("Status Code:" + bf.getInt());
           System.out.println("Error Code:" + bf.getInt());
    }
    
      public void getS_REVOKE(ByteBuffer bf)
    {   
           bf.position(40);
           System.out.println("Status Code:" + bf.getInt());
           System.out.println("Error Code:" + bf.getInt());
    }  
      
    public void getBLOCK(ByteBuffer bf)
    {
           bf.position(40);
           System.out.println("Username processing..:");
           bf.position(60);
           System.out.println("Flags processing..");
    }
    
    public void getCONNECT(ByteBuffer bf)
    {
           bf.position(40);
           System.out.println("Version..");
           
           bf.position(44);
           System.out.println("Server Authentication..");
           
           bf.position(64);
           System.out.println("Encrypt Option");
           
           bf.position(72);
           System.out.println("Number of other options:"+bf.getInt());
           
           bf.position(76);
           System.out.println("Other Option List");
           
           bf.position(77);
           System.out.println("Number of shared secrets"+bf.getInt());
           
           bf.position(81);
           System.out.println("Secret List..");
    }
    
       
       
}