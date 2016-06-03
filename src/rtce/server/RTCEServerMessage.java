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

import com.sun.corba.se.impl.util.Version;
//import com.sun.xml.internal.txw2.Document;


import static rtce.RTCEConstants.getRtcecharset;

/****THIS CLASS INTERPRETS(PARSES) AND SETS ALL VALID MESSAGES TO BE SENT AND RECEIVED BY THE SERVER RESPECTIVELY****/

public class RTCEServerMessage {

	//The request type
	private RTCEMessageType request;
	private RTCEMessageType response;
	private RTCEMessageType messageType;

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

	//The session ID for all headers
	public long sessionId;

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
	
	//These are the flags for blocked message 
	private boolean flags[];
	
	//The identifiers for the the document to access.
	private static String documentOwner;
	private static String documentTitle;
	
	public static RTCEDocument document;
	private int sectionID;

	/**
	 * Get the message type
	 * @return The message type
	 */
	public RTCEMessageType getRequest() {
		return request;
	}


	public RTCEMessageType getMessageType() {
		return messageType;
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
	
	public boolean[] getFlags() {
		return flags;
	}

	public void setFlags(boolean[] flags) {
		this.flags = flags;
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

	public void setSectionID(int sID) {
		this.sectionID = sID;		
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
	public void setTime(){
		this.timeStamp = System.nanoTime();
	}

	public long getTime(){
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

	public RTCEMessageType getResponse() {
		return response;
	}

	public String[] getSharedSecrets() {
		return sharedSecrets;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public RTCEDocument getDocument() {
		return document;
	}

	public int getSectionID() {
		return sectionID;
	}
	
	public void setResponse(RTCEMessageType response) {
		this.response = response;
	}
	
	public void setMessageType(RTCEMessageType messageType) {
		this.messageType = messageType;
	}

	public void setSharedSecrets(String[] sharedSecrets) {
		this.sharedSecrets = sharedSecrets;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public byte[] getVersion() {
		return version;
	}

	public void setVersion(byte[] version) {
		this.version = version;
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


	public byte[][] getEncryptsAsBytes(){
		return RTCEConstants.getBytesFromStrings(encryptOpts, RTCEConstants.getOptLength());
	}
	
	public byte[][] getOptsAsBytes(){
		return RTCEConstants.getBytesFromStrings(genericOpts, RTCEConstants.getOptLength());
	}
	
	public byte[][] getSecretsAsBytes(){
		return RTCEConstants.getBytesFromStrings(sharedSecrets, RTCEConstants.getSecretLength());
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
        
        //set the message pdu header
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

        //process the header information
	public void getHeader(ByteBuffer bf){
		String requestFull = new String(bf.array(), 0, 8, RTCEConstants.getRtcecharset());
		String request = "";
		for(int i = 0; i < requestFull.length(); i++){
			if(requestFull.charAt(i) == 0){
				break;
			}else{
				request += requestFull.charAt(i);
			}
		}
		//System.out.println("Request in string format: " + request);
		setRequest(RTCEMessageType.valueOf(request));
		//System.out.println(bf.position());

		bf.position(8);
		setSessionId(bf.getLong());
		setTimeStamp(bf.getLong());
		setChecksum(bf.getInt());
		setHeaderReserved1(bf.getInt());
		setHeaderReserved2(bf.getInt());
		setHeaderReserved3(bf.getInt());
		//System.out.println("Session ID:   "+ getSessionId());
		//System.out.println("Time Stamp:   "+ getTimeStamp());

		//System.out.println("Checksum: "+ getChecksum());
		//System.out.println("Reserved1:  "+ getHeaderReserved1());
		//System.out.println("Reserved 2: "+ getHeaderReserved2());
		//System.out.println("Reserved 3: "+ getHeaderReserved3());   
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
	//allocate length of buffer according to request
	public int lengthBuffer(String s)
	{
		//Allocate buffer length according to request
		switch (s) 
		{
		case "CUAUTH":
			return 130;

		case "S_TREQST":
			return 52;  

		case "S_COMMIT":
			return 1000;    

		case "ABORT":
			return 40;

		case "ECHO":
			return 40;   	

		case "LOGOFF":
			return 40; 

		case "CACK":
			return 40; 
			
		case "BLOCK":
	           return 64;	

		default: return 0;	        

		}
	}
        //returns the asciival of the request
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
		case CONNECT:
			if(genericOpts == null){
				genericOpts = new String[0];
			}
			if(sharedSecrets == null){
				sharedSecrets = new String[0];
			}
			controlPayload = new ControlMessage(40 + (8*genericOpts.length) + (16*sharedSecrets.length));
			controlPayload.setUsername(username);
			controlPayload.setEncryptOpts(encryptOpts);
			controlPayload.setGenericOpts(genericOpts);
			controlPayload.setSharedSecrets(sharedSecrets);
			controlPayload.setVersion(version);
			controlPayload.payload = controlPayload.setCONNECT(); 
			break;   
		case S_LIST:
			controlPayload = new ControlMessage(document);
			controlPayload.setS_LIST();
			break;    	   
		case S_DATA:
			controlPayload = new ControlMessage(document);
			controlPayload.setS_DATA(sectionID);
			break;  

		case S_TRESPN:
			controlPayload = new ControlMessage(24);
			controlPayload.payload = controlPayload.setS_TRESPN(token, section); 
			break; 

		case S_DENIED:
			controlPayload = new ControlMessage(8);
			controlPayload.payload = controlPayload.setS_REVOKE();
			break;    	     	   

		case ABORT:
		{}
		break;    	   
		
		case ECHO:
		{}
		break; 
                
       case S_DONE:
            controlPayload = new ControlMessage(8);
			controlPayload.payload = controlPayload.setS_DONE();
		break; 

       case BLOCK:
        	controlPayload = new ControlMessage(24);
        	if(username == null){
        		username = "";
        	}
        	if(flags == null){
        		flags = new boolean[4*8];
        	}
        	controlPayload.setUsername(username);
        	controlPayload.setFlags(flags);
       		controlPayload.payload = controlPayload.setS_BLOCK();
        	break;     	   

		case LACK:
		{} 
		break;    	   

		case S_REVOKE:
			controlPayload = new ControlMessage(8);
			controlPayload.payload = controlPayload.setS_REVOKE();
			break;     	   

		default: validflag = false;
		//System.out.println("2");  	
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
				s.getOutputStream().write(out.toByteArray(), 0, out.size());

				byte[] test = out.toByteArray();
				String s1 = new String(test, 0, 8);
				//System.out.println("OUTGOING RESPONSE: "+s1+"\n");

			}
			catch(IOException ex)
			{ System.err.println("IOException in sendMessage"); }       
		}   
	}  

	// This function receives messages from the client on the socket, server can receive only specific messages
	public void recvMessage(Socket s, RTCEMessageType request, ByteBuffer bf, RTCEServerLog log, RTCEServerRecordMgmt record, RTCEServerLog client)
	{
		//Extract header contents first, needed to make a function out of this
		getHeader(bf);
		ControlMessage control = null; 

		switch (request) 
		{
		case CUAUTH:
			control = new ControlMessage();
			control.getCUAUTH(bf); 
			username = control.getUsername();
			password = control.getPassword();
			documentOwner = control.getDocumentOwner();
			documentTitle = control.getDocumentTitle();
			encryptOpts = control.getEncryptOpts();
			genericOpts = control.getGenericOpts();
			version = control.getVersion();
			break;

			// S_TREQST for testing
		case S_TREQST:
			control = new ControlMessage();
			control.getS_TREQST(bf, s, log, record, client);
			break;   
                        
		case BLOCK:
			control = new ControlMessage();
			control.getBLOCK(bf, log, record);
			break;           

		case S_COMMIT:
			control = new ControlMessage(document);
			control.getS_COMMIT(bf, s, record, client);
			{}
			break;    

		case ABORT:
			control = new ControlMessage();
			//System.out.println("ABORT \n");
			{}
			break; 

		case ECHO:
			control = new ControlMessage();
			//System.out.println("ECHO \n");
			{}
			break;    	

		case LOGOFF:
			control = new ControlMessage();
			//System.out.println("LOGOFF \n");
			{}
			break; 

		case CACK:
			control = new ControlMessage();
			//System.out.println("CACK \n");
			{}
			break;  

		default: {}

		} //switch (request)

	} 

}   


//Control Messages for all get and set methods corresponding to pdu's
class ControlMessage extends RTCEServerMessage
{

	ByteBuffer payload;
	int messagetype;

	ControlMessage(){}

	ControlMessage(int byteSize)

	{	   

		payload = ByteBuffer.allocate(byteSize);
	}
        
        //set the docuement to be transmitted
	ControlMessage(RTCEDocument doc)
	{ 
		setDocument(doc);
	} 
        
        //DATA MESSAGES TO SEND
        
         //set the list of sections in the docuement
	public void setS_LIST()
	{	   
		int numberOfIDs = this.getDocument().resetSectionItr();	  	  
		payload = ByteBuffer.allocate(numberOfIDs*4+4);

		for(int i = 0; i < numberOfIDs; i++)
		{ payload.putInt(getDocument().getNextSectionItr().getID());  }
		payload.putInt(0);
	}
        
        //set the data in the document according to the section Id
	public void setS_DATA(int sectionID)
	{
		String sectionText = this.getDocument().getDocumentSection(sectionID).getTxt();
		payload = ByteBuffer.allocate(4+4+sectionText.length());

		payload.putInt(sectionID);
		payload.putInt(sectionText.length());
		payload.put(sectionText.getBytes(RTCEConstants.getRtcecharset()));

	}


	//ALL RECEIVED MESSAGES FROM CLIENT SIDE
        
        //get initiation from client
	public void getCUAUTH(ByteBuffer bf)
	{
		bf.position(40);
		byte ver[] = new byte[4];
		ver[0] = bf.get();
		ver[1] = bf.get();
		ver[2] = bf.get();
		ver[3] = bf.get();
		setVersion(ver);
		//System.out.println("Version: " + ver);

		bf.position(44);
		setUsername(RTCEConstants.clipString(new String(bf.array(), 44, RTCEConstants.getUsernameLength(), RTCEConstants.getRtcecharset())));
		//System.out.println("Username: " + getUsername());

		bf.position(44+RTCEConstants.getUsernameLength());
		setPassword(RTCEConstants.clipString(new String(bf.array(), 44+RTCEConstants.getUsernameLength(), RTCEConstants.getAuthStringLength(), RTCEConstants.getRtcecharset())));
		//System.out.println("Authentication: " + getPassword());

		bf.position(44+RTCEConstants.getUsernameLength()+RTCEConstants.getAuthStringLength());
		setDocumentOwner(RTCEConstants.clipString(new String(bf.array(), 44+RTCEConstants.getUsernameLength()+RTCEConstants.getAuthStringLength(), RTCEConstants.getUsernameLength(), RTCEConstants.getRtcecharset())));
		//System.out.println("Document Owner: " + getDocumentOwner());

		bf.position(44+(2*RTCEConstants.getUsernameLength())+RTCEConstants.getAuthStringLength());
		setDocumentTitle(RTCEConstants.clipString(new String(bf.array(), 44+(2*RTCEConstants.getUsernameLength())+RTCEConstants.getAuthStringLength(), RTCEConstants.getDocTitleLength(), RTCEConstants.getRtcecharset())));
		//System.out.println("Document Title: " + getDocumentTitle());

		bf.position(44+(2*RTCEConstants.getUsernameLength())+RTCEConstants.getAuthStringLength()+RTCEConstants.getDocTitleLength());
		int numEncryptOptions = bf.getInt();
		//System.out.println("Num Encrypt Options:" + numEncryptOptions);
		String encs[] = new String[numEncryptOptions];
		for(int i = 0; i < numEncryptOptions; i++){
			encs[i] = RTCEConstants.clipString(new String(bf.array(), 48+(2*RTCEConstants.getUsernameLength())+RTCEConstants.getAuthStringLength()+RTCEConstants.getDocTitleLength()+(i*RTCEConstants.getOptLength()), RTCEConstants.getOptLength(), RTCEConstants.getRtcecharset()));
			//System.out.println(encs[i]);
		}
		setEncryptOpts(encs);
		//bf.position(124);
		//System.out.println("Encrypt Option List Processing..");

		bf.position(48+(2*RTCEConstants.getUsernameLength())+RTCEConstants.getAuthStringLength()+RTCEConstants.getDocTitleLength()+(numEncryptOptions*RTCEConstants.getOptLength()));
		int numGenOptions = bf.getInt();
		//System.out.println("Num Other Options: " + numGenOptions);
		String gens[] = new String[numGenOptions];
		for(int i = 0; i < numGenOptions; i++){
			gens[i] = RTCEConstants.clipString(new String(bf.array(), 52+(2*RTCEConstants.getUsernameLength())+RTCEConstants.getAuthStringLength()+RTCEConstants.getDocTitleLength()+(numEncryptOptions*RTCEConstants.getOptLength())+(i*RTCEConstants.getOptLength()), RTCEConstants.getOptLength(), RTCEConstants.getRtcecharset()));
			//System.out.println(gens[i]);
		}
		setGenericOpts(gens);
		//bf.position(129);
		//System.out.println("Num Other Options..");


	}

        //get request from client for token
public void getS_TREQST(ByteBuffer bf, Socket s, RTCEServerLog log, RTCEServerRecordMgmt record, RTCEServerLog client)
	{   
               
                double token = -1;
		bf.position(40);
                
                    int section = bf.getInt();
                    RTCEServer service = new RTCEServer();
                    
                    //if section if free
                    if(record.checkFreeSection(section) && record.clientHasToken(client)!=true){
                     token = record.tokenGrant(client, section); //grant client token
                     service.sendResponse(RTCEMessageType.S_TRESPN, token, section, s); //send approval token
                        }
                    
                    else{
                        service.sendResponse(RTCEMessageType.S_DENIED, token, section, s); //else send a denial
                    }
                   
                    //No options now = 0
		//System.out.println("Options: "+ bf.get());
                      
	}

	
        //get the commit of data from client
	public void getS_COMMIT(ByteBuffer bf, Socket s, RTCEServerRecordMgmt control, RTCEServerLog client) 
	{
		bf.position(40);
				
		double token = bf.getDouble();
		int prevID = bf.getInt();
		int sID = bf.getInt();		
                int len = bf.getInt();
            RTCEServerMessage response = new RTCEServerMessage();
            String sectionTxt = new String(bf.array(), 60, len);
  	    //System.out.println(token);
            //System.out.println(control.checkClientToken(client));
            
                if(token == control.checkClientToken(client))  //if client has the specific token then commit changes
                {  
                    document.processCommit(prevID, sID, sectionTxt);
                    //System.out.println("S_COMMIT="+sID+" txt=" + sectionTxt);
                     
                    response = new RTCEServerMessage();
                    response.setRequest(RTCEMessageType.S_DONE); //send a done that is commit was successful
                    response.setSessionId(client.getSessionId());
                    response.sendMessage(s, RTCEMessageType.S_DONE, -1, -1);
                    control.tokenRevoke(client); //revoke the token from the client and free the section 
                    
                //This would be the location to push out this to all connected clients
                }
                
                else{
                      // System.out.println("false"); //print false just for debugging, client doesn't get a denial if changes were not performed
                       
                    }
        }
	//ALL SEND MESSAGES FROM SERVER SIDE
	public ByteBuffer setS_TRESPN(double tok, int sec) //send the response for the token corresponding to the section
	{
                byte[] length = new byte[4];
                byte[] priviledge = new byte[8];
                
                for(int i=0; i<4; i++)
			length[i] = (byte)0;

		for(int i=0; i<8; i++)
			priviledge[i] = (byte)0;

		ByteBuffer b = ByteBuffer.allocate(24);
		b.putDouble(tok);
                b.putInt(sec);
                b.put(length);
		b.put(priviledge);

		return b;
	}
	
        //SOME STATUS MESSAGES
        
	public ByteBuffer setS_REVOKE()
	{
		//status code 1 revoke, error 1 -> Unique interpretation, token revoked due by application level
		int statuscode = 1;
		int error = 1;
		ByteBuffer b = ByteBuffer.allocate(8);
		b.putInt(statuscode);
		b.putInt(error);
		return b;
	}
        
        public ByteBuffer setS_DONE()
	{
		//status code 1 revoke, error 0 -> Unique interpretation, Commit performed by application
		int statuscode = 1;
		int error = 0;
		ByteBuffer b = ByteBuffer.allocate(8);
		b.putInt(statuscode);
		b.putInt(error);
		return b;
	}
        
	public ByteBuffer setS_DENIED() 
	{
		//status code 1 denied, error 1 -> Unique interpretation, token in use
		int statuscode = 1;
		int error = 1;
		ByteBuffer b = ByteBuffer.allocate(8);
		b.putInt(statuscode);
		b.putInt(error);
		return b;
	}
        
        //REQUEST TO BLOCK A  USER-> ONLY CAN BE DONE BY OWNER THREAD IN IMPLEMENTATION 
	  public void getBLOCK(ByteBuffer bf, RTCEServerLog log, RTCEServerRecordMgmt control)
	    {
	           bf.position(40);
	           setUsername(RTCEConstants.clipString(new String(bf.array(), 40, RTCEConstants.getUsernameLength(), RTCEConstants.getRtcecharset())));
	           //System.out.println("Username: " + getUsername());
	           bf.position(40+RTCEConstants.getUsernameLength());
	           boolean blockFlags[] = new boolean[4*8];
	           byte readFlags[] = new byte[4];
	           readFlags[0] = bf.get();
	           readFlags[1] = bf.get();
	           readFlags[2] = bf.get();
	           readFlags[3] = bf.get();
	           //bf.get(readFlags, 40+RTCEConstants.getUsernameLength(), 4);
	           blockFlags = readBits(readFlags);
	           setFlags(blockFlags);
	           //System.out.println("Flags processing..");
	           for(int i = 0; i < blockFlags.length; i++){
	        	   //System.out.print(blockFlags[i] + ";");
	           }
	           System.out.println();
	           boolean exists = log.setBlock(getUsername()); //block the client
	           
	           if(log.getBlockedClientId(getUsername())!=null)
                       control.tokenRevoke(log.getBlockedClientId(getUsername()));//revoke any token if client is blocked
                   
	    }
	  
	  public boolean[] readBits(byte bitFlags[]){
	    	boolean flags[] = new boolean[bitFlags.length * 8];
	    	for(int i = 0; i < bitFlags.length; i++){
	    		for(int j = 0; j < 8; j++){
	    			if(((bitFlags[i] >> j) & 1) == 1){
	    				flags[(i*8)+j] = true;
	    			}else{
	    				flags[(i*8)+j] = false;
	    			}
	    		}
	    	}
	    	return flags;
	    }
	
        //INFORM THE USER THAT HE IS BLOCKED
	public ByteBuffer setS_BLOCK()
	{
		byte uname[] = getUsernameChars();
		byte blockFlags[] = flagsToBytes(getFlags());
		ByteBuffer b = ByteBuffer.allocate(RTCEConstants.getUsernameLength()+4);
		b.put(uname);
		b.put(blockFlags);
		return b; 
	}

	public byte[] flagsToBytes(boolean flags[]){
		byte bytes[] = new byte[flags.length / 8];
		int value;
		for(int i = 0; i < bytes.length; i++){
			bytes[i] = 0;
			for(int j = 0; j < 8; j++){
				if(flags[(i*8)+j]){
					value = (int) Math.pow(2, (7-j));
					bytes[i] += value;
				}
			}
		}
		return bytes;
	}
        
        //SET CONNECT MESSAGE FROM SERVER SIDE CONNECTION
	public ByteBuffer setCONNECT()
	{      
		
		byte version[] = getVersion();
		byte authentication[] = getUsernameChars();
		byte encrypt_option[] = getEncryptsAsBytes()[0];
		byte generics[][] = getOptsAsBytes();
		int num_other_options = generics.length;
		byte secret_list[][] = getSecretsAsBytes();
		int num_shared_secrets = secret_list.length;

		ByteBuffer b = ByteBuffer.allocate(40 + (8*num_other_options) + (16*num_shared_secrets));
		
		b.put(version);
		b.put(authentication);
		b.put(encrypt_option);
		b.putInt(num_other_options);
		for(int i = 0; i < generics.length; i++){
			b.put(generics[i]);
		}
		//b.put(other_option_list);
		b.putInt(num_shared_secrets);
		for(int i = 0; i < secret_list.length; i++){
			b.put(secret_list[i]);
		}
		//b.put(secret_list);

		return b; 
	}

}
