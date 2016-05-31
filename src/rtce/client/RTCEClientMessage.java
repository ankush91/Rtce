package rtce.client;

import rtce.RTCEMessageType;
import rtce.RTCEConstants;
import rtce.RTCEDocument;
import rtce.server.ControlMessage;
import rtce.server.ServerLog;
import rtce.server.ServerRecordMgmt;

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
	
	//The list of shared secrets
	private String sharedSecrets[];

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

	//The version
	private byte version[];
	
	//The three pieces of data for a Commit
	private int    Commit_sID;
	private int    Commit_prevID;
	private String Commit_txt;
	private double    Commit_token;
	
        
        //The pieces for token request for a section and response token
        private static int Request_sID;
        private static double Response_token;

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
	
	public String[] getSharedSecrets() {
		return sharedSecrets;
	}

	public void setSharedSecrets(String[] sharedSecrets) {
		this.sharedSecrets = sharedSecrets;
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
	
	public byte[] getVersion() {
		return version;
	}

	public void setVersion(byte[] version) {
		this.version = version;
	}

	public int getCommit_sID() {
		return Commit_sID;
	}
        
	public void setCommit_sID(int commit_sID) {
		Commit_sID = commit_sID;
	}

	public int getCommit_prevID() {
		return Commit_prevID;
	}

	public void setCommit_prevID(int commit_prevID) {
		Commit_prevID = commit_prevID;
	}

	public String getCommit_txt() {
		return Commit_txt;
	}

	public void setCommit_txt(String commit_txt) {
		Commit_txt = commit_txt;
	}

	public double getCommit_token() {
		return Commit_token;
	}
        
        public int getSectionId(){
            return Request_sID;
        }
        
        public void setSectionId(int section){
           Request_sID = section;
           
        }
        
        public double getResponseToken(){
            return Response_token;
        }
          
        
        public void setToken(double token){
           Response_token = token;
        }

	public void setCommit_token(double commit_token) {
		Commit_token = commit_token;
	}

	/**
	 * Get the username as a byte array
	 * @return username as a byte array
	 */
	public byte[] getUsernameChars(){
		return RTCEConstants.getStringAsBytes(username, RTCEConstants.getUsernameLength());
	}
	
	/**
	 * Get the password as a byte array
	 * @return password as a byte array
	 */
	public byte[] getPasswordChars(){
		return RTCEConstants.getStringAsBytes(password, RTCEConstants.getAuthStringLength());
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
			for(int i = 0; i < requestBytes.length; i++){
				requestName[i] = requestBytes[i];
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
        
	public byte[][] getEncryptsAsBytes(){
		return RTCEConstants.getBytesFromStrings(encryptOpts, RTCEConstants.getOptLength());
	}
	
	public byte[][] getOptsAsBytes(){
		return RTCEConstants.getBytesFromStrings(genericOpts, RTCEConstants.getOptLength());
	}
	
    public ByteBuffer setHeader(RTCEMessageType request)
    {

        setRequest(request);
        ByteBuffer bbuf = ByteBuffer.allocate(40);
        bbuf.put(getRequestChars());
        bbuf.putLong(getSessionId());
    	bbuf.putLong(getTime());
    	bbuf.putInt(getChecksum());
    	bbuf.putInt(getHeaderReserved1());
    	bbuf.putInt(getHeaderReserved2());
    	bbuf.putInt(getHeaderReserved3());
        return bbuf;
        
    }   
    
    /**
     * Generate the byte buffer for the message header
     * @return the byte buffer for the header of the message
     */
    public ByteBuffer setHeader(){
    	ByteBuffer bbuf = ByteBuffer.allocate(40);
    	bbuf.put(getRequestChars());
    	bbuf.putLong(getSessionId());
    	bbuf.putLong(getTime());
    	bbuf.putInt(getChecksum());
    	bbuf.putInt(getHeaderReserved1());
    	bbuf.putInt(getHeaderReserved2());
    	bbuf.putInt(getHeaderReserved3());
    	return bbuf;
    }
   
     public void getHeader(ByteBuffer bf)
       {
    	String requestFull = new String(bf.array(), 0, 8, RTCEConstants.getRtcecharset());
 		String request = "";
 		for(int i = 0; i < requestFull.length(); i++){
 			if(requestFull.charAt(i) == 0){
 				break;
 			}else{
 				request += requestFull.charAt(i);
 			}
 		}

  		System.out.println("Request in string format: " + request);
  		setRequest(RTCEMessageType.valueOf(request));
  		System.out.println(bf.position());

  		bf.position(8);
  		setSessionId(bf.getLong());
  		setTimeStamp(bf.getLong());
  		setChecksum(bf.getInt());
  		setHeaderReserved1(bf.getInt());
  		setHeaderReserved2(bf.getInt());
  		setHeaderReserved3(bf.getInt());
  		/* FOR DEBUG
  		System.out.println("Session ID:   "+ getSessionId());
  		System.out.println("Time Stamp:   "+ getTimeStamp());

  		System.out.println("Checksum: "+ getChecksum());
  		System.out.println("Reserved1:  "+ getHeaderReserved1());
  		System.out.println("Reserved 2: "+ getHeaderReserved2());
  		System.out.println("Reserved 3: "+ getHeaderReserved3());  
          */
       }  
    
    //This function sets all the data needed to send a S_COMMIT message
    public void setCommitData(double token, int prevID, int sID, String newText)
    {
      Commit_token  = token;
      Commit_prevID = prevID; 
      Commit_sID    = sID;
      Commit_txt    = newText;
    }
    
    // This function builds and transmits the Message on the supplied socket
    public void sendMessage(Socket s, RTCEMessageType option, double token, int section)
    {
        boolean validflag = true;
        
        //header information in messageheader
        ByteBuffer messageHeader = setHeader(option);
        
        //Initialization of Control Message class object
     
        ControlMessage controlPayload = null;
        
       //Use the request type to build the message
       //CLIENT CAN SEND ONLY SPECIFIC MESSAGES, option with request for now
       switch (option) 
       {
      case CUAUTH:
          controlPayload = new ControlMessage(88 + (8*(encryptOpts.length + genericOpts.length)));
          controlPayload.setUsername(username);
          controlPayload.setPassword(password);
          controlPayload.setDocumentOwner(documentOwner);
          controlPayload.setDocumentTitle(documentTitle);
          controlPayload.setEncryptOpts(encryptOpts);
          controlPayload.setGenericOpts(genericOpts);
          controlPayload.setVersion(version);
          controlPayload.payload = controlPayload.setCUAUTH();
    	  break;
     
        // S_TREQST for testing
       case S_TREQST:
         
            controlPayload = new ControlMessage(12);
            controlPayload.payload = controlPayload.setSTREQST(section);
     	  break;    
          
          
          
        case S_COMMIT:
            controlPayload = new ControlMessage(8+4+4+4+Commit_txt.length());
            controlPayload.payload.putDouble(Commit_token);
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
                     String s1 = new String(test, 0, 8, RTCEConstants.getRtcecharset());
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

          case "S_DONE":
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
          version = control.getVersion();
          username = control.getUsername();
          encryptOpts = control.getEncryptOpts();
          genericOpts = control.getGenericOpts();
          sharedSecrets = control.getSharedSecrets();
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

       case S_DONE:
			control = new ControlMessage();
			control.getS_DONE(bf, s, log, record, client);
			{}
			break; 
     	  
       default: {}
    	        	
       } //switch (request)
            
    }  
}



//Control Messages extends RTCE
class ControlMessage extends RTCEClientMessage
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
       
       
    	byte[] version = getVersion();
        
    	byte username[] = getUsernameChars();
            
    	byte authentic[] = getPasswordChars();
            
    	byte docOwner[] = getDocumentOwnerChars();
             
    	byte docName[] = getDocumentTitleChars();
             
    	byte encrypts[][] = getEncryptsAsBytes();
    	byte generics[][] = getOptsAsBytes();
    	
    	int num_encrypt_opts = encrypts.length;
    	int num_generic_opts = generics.length;
            
        ByteBuffer bf = ByteBuffer.allocate(88 + (8*(num_encrypt_opts + num_generic_opts)));
        /*System.out.println("Version: " + getVersion());
        System.out.println("Username: " + getUsername());
        System.out.println("Password: " + getPassword());
        System.out.println("DocOwner: " + getDocumentOwner());
        System.out.println("DocName: " + getDocumentTitle());*/
        bf.put(version);
        bf.put(username);
        bf.put(authentic);
        bf.put(docOwner);
        bf.put(docName);
        bf.putInt(num_encrypt_opts);
        for(int i = 0; i < num_encrypt_opts; i++){
        	bf.put(encrypts[i]);
        }
        bf.putInt(num_generic_opts);
    	for(int i = 0; i < num_generic_opts; i++){
    		bf.put(generics[i]);
    	}
        return bf;
        
    }
   
     public ByteBuffer setSTREQST(int section) 
    {
         
        System.out.println(section);
        
        //static section lengths
        int length = 0;
        byte[] options = new byte[4];
        
        //Not using set U and V bit
        //options[0] = (byte) (options[0] | (1 << 0) | (1 << 1));
        
        ByteBuffer b = ByteBuffer.allocate(12);
        b.putInt(section);
        b.putInt(length);
        b.put(options);
        return b; 
    }
     
      
       
       //ALL RECEIVED MESSAGES FROM SERVER SIDE
       public void getS_TRESPN(ByteBuffer bf)
    {   
           bf.position(40);
           
           System.out.println("token processing..");
           
           bf.position(48);
           if(getSectionId()==bf.getInt())
           {
               bf.position(40);
               setToken(bf.getDouble());
           }
               
           //System.out.println("length processing..");    
           bf.getInt();
               
           //System.out.println("additional priviledges..");
           bf.position(56);
           
          
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
           	byte ver[] = new byte[4];
   		 	ver[0] = bf.get();
   			ver[1] = bf.get();
   			ver[2] = bf.get();
   			ver[3] = bf.get();
   			setVersion(ver);
   			System.out.println("Version: " + ver);
           
           bf.position(44);
           setUsername(RTCEConstants.clipString(new String(bf.array(), 44, RTCEConstants.getUsernameLength(), RTCEConstants.getRtcecharset())));
           System.out.println("Server Authentication: " + getUsername());
           
           bf.position(44+RTCEConstants.getUsernameLength());
           String enc[] = new String[1];
           enc[0] = RTCEConstants.clipString(new String(bf.array(), 44+RTCEConstants.getUsernameLength(), RTCEConstants.getOptLength(), RTCEConstants.getRtcecharset()));
           setEncryptOpts(enc);
           System.out.println("Encrypt Option: " + getEncryptOpts()[0]);
           
           bf.position(44+RTCEConstants.getUsernameLength()+RTCEConstants.getOptLength());
           int numGenOpts = bf.getInt();
           System.out.println("Number of other options: "+ numGenOpts);
           String gen[] = new String[numGenOpts];
           for(int i = 0; i < numGenOpts; i++){
        	   gen[i] = RTCEConstants.clipString(new String(bf.array(), 48+RTCEConstants.getUsernameLength()+RTCEConstants.getOptLength()+(i*RTCEConstants.getOptLength()), RTCEConstants.getOptLength(), RTCEConstants.getRtcecharset()));
        	   System.out.println(gen[i]);
           }
           setGenericOpts(gen);
           //bf.position(48+RTCEConstants.getUsernameLength()+RTCEConstants.getOptLength());
           //System.out.println("Other Option List");
           
           bf.position(48+RTCEConstants.getUsernameLength()+RTCEConstants.getOptLength()+(numGenOpts*RTCEConstants.getOptLength()));
           int numSecrets = bf.getInt();
           System.out.println("Number of shared secrets: "+ numSecrets);
           String sec[] = new String[numSecrets];
           for(int i = 0; i < numSecrets; i++){
        	   sec[i] = RTCEConstants.clipString(new String(bf.array(), 48+RTCEConstants.getUsernameLength()+RTCEConstants.getOptLength()+(numGenOpts*RTCEConstants.getOptLength())+(i*RTCEConstants.getSecretLength()), RTCEConstants.getSecretLength(), RTCEConstants.getRtcecharset()));
        	   System.out.println(sec[i]);
           }
           setSharedSecrets(sec);
           //bf.position(81);
           //System.out.println("Secret List..");
    }
    
    public void getS_DONE(ByteBuffer bf, Socket s, ServerLog log, ServerRecordMgmt record, ServerLog client)

	{       
		bf.position(40);
                //default status and error message
		int status = bf.getInt();
                int error = bf.getInt();
               
                record.tokenRevoke(client);
                
	}
       
}