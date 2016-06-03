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

/**
 * RTCEClientMessage
 * The message object for the client.  Implements recieving server messages and sending client messages
 * @author Ankush Israney, Edwin Dauber, Anthony Emma, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 */
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

	//These are the flags for blocked message 
	private boolean flags[];

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

	/**
	 * Get the username
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get the password
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set the request type
	 * @param request
	 */
	public void setRequest(RTCEMessageType request) {
		this.request = request;
	}

	/**
	 * Set the username
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Set the password
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get the encryption options
	 * @return the encryption options
	 */
	public String[] getEncryptOpts() {
		return encryptOpts;
	}

	/**
	 * Set the encryption options
	 * @param encryptOpts
	 */
	public void setEncryptOpts(String[] encryptOpts) {
		this.encryptOpts = encryptOpts;
	}

	/**
	 * Get the generic options
	 * @return the generic options
	 */
	public String[] getGenericOpts() {
		return genericOpts;
	}

	/**
	 * Set the generic options
	 * @param genericOpts
	 */
	public void setGenericOpts(String[] genericOpts) {
		this.genericOpts = genericOpts;
	}

	/**
	 * Get the shared secrets
	 * @return the shared secrets
	 */
	public String[] getSharedSecrets() {
		return sharedSecrets;
	}

	/**
	 * Set the shared secrets
	 * @param sharedSecrets
	 */
	public void setSharedSecrets(String[] sharedSecrets) {
		this.sharedSecrets = sharedSecrets;
	}

	/**
	 * Set the document
	 * @param doc
	 */
	public void setDocument(RTCEDocument doc) {
		this.document = doc;
	}	

	/**
	 * Get the server response
	 * @return the server response
	 */
	public RTCEMessageType getServerResponse() {
		return serverResponse;
	}

	/**
	 * Set the server response
	 * @param serverResponse
	 */
	public void setServerResponse(RTCEMessageType serverResponse) {
		this.serverResponse = serverResponse;
	}

	/**
	 * Get the document owner
	 * @return the document owner
	 */
	public String getDocumentOwner() {
		return documentOwner;
	}

	/**
	 * Set the document owner
	 * @param documentOwner
	 */
	public void setDocumentOwner(String documentOwner) {
		this.documentOwner = documentOwner;
	}

	/**
	 * Get the document title
	 * @return the document title
	 */
	public String getDocumentTitle() {
		return documentTitle;
	}

	/**
	 * Set the document title
	 * @param documentTitle
	 */
	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}

	/**
	 * Get the timestamp
	 * @return the timestamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Set the timestamp
	 * @param timeStamp
	 */
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * Get the document
	 * @return the document
	 */
	public RTCEDocument getDocument() {
		return document;
	}

	/**
	 * Get the version
	 * @return the version
	 */
	public byte[] getVersion() {
		return version;
	}

	/**
	 * Set the version
	 * @param version
	 */
	public void setVersion(byte[] version) {
		this.version = version;
	}

	/**
	 * Get the commit section id
	 * @return the commit section id
	 */
	public int getCommit_sID() {
		return Commit_sID;
	}

	/**
	 * Set the commit section id
	 * @param commit_sID
	 */
	public void setCommit_sID(int commit_sID) {
		Commit_sID = commit_sID;
	}

	/**
	 * Get the commit previous id
	 * @return the commit previous id
	 */
	public int getCommit_prevID() {
		return Commit_prevID;
	}

	/**
	 * Set the commit previous id
	 * @param commit_prevID
	 */
	public void setCommit_prevID(int commit_prevID) {
		Commit_prevID = commit_prevID;
	}

	/**
	 * Get the commit text
	 * @return the commit text
	 */
	public String getCommit_txt() {
		return Commit_txt;
	}

	/**
	 * Set the commit text
	 * @param commit_txt
	 */
	public void setCommit_txt(String commit_txt) {
		Commit_txt = commit_txt;
	}

	/**
	 * Get the commit token
	 * @return the commit token
	 */
	public double getCommit_token() {
		return Commit_token;
	}

	/**
	 * Get the token section id
	 * @return the token section id
	 */
	public int getSectionId(){
		return Request_sID;
	}

	/**
	 * Set the token section id
	 * @param section
	 */
	public void setSectionId(int section){
		Request_sID = section;

	}

	/**
	 * get the token value
	 * @return the token value
	 */
	public double getResponseToken(){
		return Response_token;
	}

	/**
	 * Set the token value
	 * @param token
	 */
	public void setToken(double token){
		Response_token = token;
	}

	/**
	 * Set the commit token value
	 * @param commit_token
	 */
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

	/**
	 * Set the request type from bytes
	 * @param requestChars
	 */
	public void setRequest(byte[] requestChars){
		String requestName = new String(requestChars, RTCEConstants.getRtcecharset());
		request = RTCEMessageType.valueOf(requestName);
	}

	/**
	 * Get the request type as bytes
	 * @return the request type as bytes
	 */
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

	/**
	 * Get the session id
	 * @return the session id
	 */
	public long getSessionId() {
		return sessionId;
	}

	/**
	 * Set the session id
	 * @param sessionId
	 */
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;              
	}

	/**
	 * Get the checksum
	 * @return the checksum
	 */
	public int getChecksum() {
		return checksum;
	}

	/**
	 * Set the checksum
	 * @param checksum
	 */
	public void setChecksum(int checksum) {
		this.checksum = checksum;
	}

	/**
	 * Set the timestamp
	 */
	public void setTime()
	{
		this.timeStamp = System.nanoTime();
	}

	/**
	 * Get the timestamp
	 * @return the timestamp
	 */
	public long getTime()
	{
		return timeStamp;
	}

	/**
	 * Get the reserved header field 1
	 * @return reserved header field 1
	 */
	public int getHeaderReserved1() {
		return headerReserved1;
	}

	/**
	 * Set the reserved header field 1
	 * @param headerReserved1
	 */
	public void setHeaderReserved1(int headerReserved1) {
		this.headerReserved1 = headerReserved1;
	}

	/**
	 * Get the reserved header field 2
	 * @return reserved header field 2
	 */
	public int getHeaderReserved2() {
		return headerReserved2;
	}

	/**
	 * Set the reserved header field 2
	 * @param headerReserved2
	 */
	public void setHeaderReserved2(int headerReserved2) {
		this.headerReserved2 = headerReserved2;
	}

	/**
	 * Get the reserved header field 3
	 * @return reserved header field 3
	 */
	public int getHeaderReserved3() {
		return headerReserved3;
	}

	/**
	 * Set the reserved header field 3
	 * @param headerReserved3
	 */
	public void setHeaderReserved3(int headerReserved3) {
		this.headerReserved3 = headerReserved3;
	}

	/**
	 * Get encrypt options as bytes
	 * @return encrypt options as bytes
	 */
	public byte[][] getEncryptsAsBytes(){
		return RTCEConstants.getBytesFromStrings(encryptOpts, RTCEConstants.getOptLength());
	}

	/**
	 * Get generic options as bytes
	 * @return generic options as bytes
	 */
	public byte[][] getOptsAsBytes(){
		return RTCEConstants.getBytesFromStrings(genericOpts, RTCEConstants.getOptLength());
	}

	/**
	 * Get flags
	 * @return flags
	 */
	public boolean[] getFlags() {
		return flags;
	}

	/**
	 * Set flags
	 * @param flags
	 */
	public void setFlags(boolean[] flags) {
		this.flags = flags;
	}

	/**
	 * get token request section id
	 * @return section id
	 */
	public static int getRequest_sID() {
		return Request_sID;
	}

	/**
	 * set token request section id
	 * @param request_sID
	 */
	public static void setRequest_sID(int request_sID) {
		Request_sID = request_sID;
	}

	/**
	 * get token value
	 * @return token value
	 */
	public static double getResponse_token() {
		return Response_token;
	}

	/**
	 * set token value
	 * @param response_token
	 */
	public static void setResponse_token(double response_token) {
		Response_token = response_token;
	}

	/**
	 * set the header
	 * @param request
	 * @return the byte buffer containing the message
	 */
	public ByteBuffer setHeader(RTCEMessageType request)
	{
		setTime();
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

	/**
	 * Read the header of a recieved message
	 * @param bf - the byte buffer of the header
	 */
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
		/* FOR DEBUG
  		System.out.println("Session ID:   "+ getSessionId());
  		System.out.println("Time Stamp:   "+ getTimeStamp());

  		System.out.println("Checksum: "+ getChecksum());
  		System.out.println("Reserved1:  "+ getHeaderReserved1());
  		System.out.println("Reserved 2: "+ getHeaderReserved2());
  		System.out.println("Reserved 3: "+ getHeaderReserved3());  
		 */
	}  

	/**
	 * This function sets all the data needed to send a S_COMMIT message
	 * @param token
	 * @param prevID
	 * @param sID
	 * @param newText
	 */
	public void setCommitData(double token, int prevID, int sID, String newText)
	{
		Commit_token  = token;
		Commit_prevID = prevID; 
		Commit_sID    = sID;
		Commit_txt    = newText;
	}

	/**
	 *  This function builds and transmits the Message on the supplied socket
	 * @param s
	 * @param option
	 * @param token
	 * @param section
	 */
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

		case BLOCK:
			controlPayload = new ControlMessage(24);
			controlPayload.setUsername(username);
			controlPayload.setFlags(flags);
			controlPayload.payload = controlPayload.setS_BLOCK();
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
				String s1 = new String(test, 0, 8, RTCEConstants.getRtcecharset());
				//System.out.println("OUTGOING REQUEST:  " + s1 + "\n");

			}
			catch(IOException ex) 
			{ System.err.println("IOException in sendMessage"); }       
		}   
	}   


	/**
	 * Get the maximum length of the received message
	 * @param s
	 * @return the maximum length by message type for our implementation
	 */
	public int lengthBuffer(String s)
	{

		//Allocate buffer length according to request
		switch (s) 
		{
		case "CONNECT":
			return 104;

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

	/**
	 * returns the asciival of the request
	 * @param asciiVal
	 * @return asciivalue of the request
	 */
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

	/**
	 *  This function builds and transmits the Message on the supplied socket
	 * @param s
	 * @param request
	 * @param bf
	 */
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


			bf.position(40);
			document.clearOrder();
			while (true)
			{
				int temp = bf.getInt();            	
				document.setOrder(temp);

				if (temp == 0)
				{break;}              
			}
			break;

		case S_DATA:

			bf.position(40);
			int sID = bf.getInt();
			int txtLen = bf.getInt();
			String sectionTxt = new String(bf.array(), 48, txtLen);

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
			System.out.println("echo received back.. \n");
			{}
			break;    	

		case LACK:
			//System.out.println("LACK \n");
		{}
		break;  

		case S_DONE:
			control = new ControlMessage();
			control.getS_DONE(bf, s);
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

	/**
	 * Create blank message
	 */
	ControlMessage(){}

	/**
	 * Create message with set size
	 * @param byteSize
	 */
	ControlMessage(int byteSize)
	{
		payload = ByteBuffer.allocate(byteSize);
	}

	/*ALL SENDING CONTROL MESSAGES*/

	/**
	 * Get the bytebuffer containing the CUAUTH message
	 * @return the bytebuffer containing the CUAUTH message
	 */
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

	/**
	 * Get the bytebuffer containing the S_TREQST message
	 * @return the bytebuffer containing the S_TREQST message
	 */
	public ByteBuffer setSTREQST(int section) 
	{



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
	/**
	 * Translate the S_TRESPN message
	 * @param bf
	 */
	public void getS_TRESPN(ByteBuffer bf)
	{   
		System.out.println("Got Access to update the requested section!");
		bf.position(40);



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

	/**
	 * Translate the S_DENIED message
	 * @param bf
	 */
	public void getS_DENIED(ByteBuffer bf)
	{   
		System.out.println("Sorry the section is not free right now..");
		bf.position(40);
		// System.out.println("Status Code:" + bf.getInt());
		//System.out.println("Error Code:" + bf.getInt());
	}

	/**
	 * Translate the S_REVOKE message
	 * @param bf
	 */
	public void getS_REVOKE(ByteBuffer bf)
	{   
		System.out.println("Access to update is revoked due to timeout. You need to make a new request again");
		bf.position(40);
		// System.out.println("Status Code:" + bf.getInt());
		//System.out.println("Error Code:" + bf.getInt());
	}  

	/**
	 * Translate the S_BLOCK message
	 * @param bf
	 */
	public void getBLOCK(ByteBuffer bf)
	{
		System.out.println("Sorry you are blocked temporarily..");
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
			// System.out.print(blockFlags[i] + ";");
		}
		System.out.println();
	}

	/**
	 * Read bytes as flags
	 * @param bitFlags
	 * @return flags
	 */
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

	/**
	 * Translate the CONNECT message
	 * @param bf
	 */
	public void getCONNECT(ByteBuffer bf)
	{
		bf.position(40);
		byte ver[] = new byte[4];
		ver[0] = bf.get();
		ver[1] = bf.get();
		ver[2] = bf.get();
		ver[3] = bf.get();
		setVersion(ver);
		//	System.out.println("Version: " + ver);

		bf.position(44);
		setUsername(RTCEConstants.clipString(new String(bf.array(), 44, RTCEConstants.getUsernameLength(), RTCEConstants.getRtcecharset())));
		// System.out.println("Server Authentication: " + getUsername());

		bf.position(44+RTCEConstants.getUsernameLength());
		String enc[] = new String[1];
		enc[0] = RTCEConstants.clipString(new String(bf.array(), 44+RTCEConstants.getUsernameLength(), RTCEConstants.getOptLength(), RTCEConstants.getRtcecharset()));
		setEncryptOpts(enc);
		// System.out.println("Encrypt Option: " + getEncryptOpts()[0]);

		bf.position(44+RTCEConstants.getUsernameLength()+RTCEConstants.getOptLength());
		int numGenOpts = bf.getInt();
		// System.out.println("Number of other options: "+ numGenOpts);
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
		// System.out.println("Number of shared secrets: "+ numSecrets);
		String sec[] = new String[numSecrets];
		for(int i = 0; i < numSecrets; i++){
			sec[i] = RTCEConstants.clipString(new String(bf.array(), 48+RTCEConstants.getUsernameLength()+RTCEConstants.getOptLength()+(numGenOpts*RTCEConstants.getOptLength())+(i*RTCEConstants.getSecretLength()), RTCEConstants.getSecretLength(), RTCEConstants.getRtcecharset()));
			System.out.println(sec[i]);
		}
		setSharedSecrets(sec);
		//bf.position(81);
		//System.out.println("Secret List..");
	}

	/**
	 * Get the bytebuffer containing the S_BLOCK message
	 * @return the bytebuffer containing the S_BLOCK message
	 */
	public ByteBuffer setS_BLOCK()
	{
		/*byte[] username = new byte[20];
		byte[] flag = new byte[1];
		byte[] reserved = new byte[3];*/
		byte uname[] = getUsernameChars();
		byte blockFlags[] = flagsToBytes(getFlags());
		ByteBuffer b = ByteBuffer.allocate(RTCEConstants.getUsernameLength()+4);
		b.put(uname);
		b.put(blockFlags);
		return b; 
	}

	/**
	 * Turn flags into bytes
	 * @param flags 
	 * @return byte representation of flags
	 */
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

	/**
	 * Translate the S_DONE message
	 * @param bf
	 */
	public void getS_DONE(ByteBuffer bf, Socket s)

	{       
		System.out.println("document updated..");
		bf.position(40);
		//default status and error message
		int status = bf.getInt();
		int error = bf.getInt();


	}

}
