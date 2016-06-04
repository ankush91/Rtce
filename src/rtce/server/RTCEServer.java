
/**

 * @cs544
 * @author GROUP 4 Ankush Israney, Edwin Dauber, Anthony Emma, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 * 
 * * RTCEServer - This is the most important class of the server. It consists of the main driver to implement the statefulness for the client
 * The driver consists of a call to the session driver after a session is established.
 * It also contains calls to the sendresponse, getname and process messages which call to the RTCEServerMessage.
 * The main function has a call to the discovery thread for the extra credit portion of the protocol.
 * The concurrency features of the server are also implemented in the main function using a java thread model.
 */

package rtce.server;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static rtce.RTCEConstants.getRtcecharset;
import rtce.RTCEMessageType;
import rtce.RTCEDocument;


public class RTCEServer implements Runnable //THIS CLASS IMPLEMENTS THE MAIN DRIVER FOR ALL THE CLIENT THREADS WITH CONTROL FLOW OF STATEFUL DFA
{
	//Various declairations
	Socket sock; 
	InputStream recvStream;  
	OutputStream sendStream; 
	String request;  
	String response;
	static RTCEServerLog log;
	RTCEServerAuth sauth;
	static RTCEDocument doc1 = new RTCEDocument(1); 
	static RTCEServerRecordMgmt control;
	boolean flagConn;
	private boolean tokenWaitTimeout;
	private Timer tokenTimer;
	private boolean blockTimeout;
	private boolean blockWaitTimeout;
	private Timer blockTimer;

	/**
	 * Create emtpy server object
	 */
	RTCEServer(){}

	/**
	 * Create server object
	 * @param init
	 */
	RTCEServer(int init){
		//initialize the log and client record information for tokens
		//CONCURRENT
		log = new RTCEServerLog();        
		control = new RTCEServerRecordMgmt(); 
	}

	/**
	 * Create server on socket
	 * @param s - socket
	 * @throws IOException
	 */
	RTCEServer (Socket s) throws IOException // initalize the socket for a thread
	{
		sock = s;
		recvStream = sock.getInputStream();
		sendStream = sock.getOutputStream();

		flagConn = true;
		tokenWaitTimeout = false;
		blockTimeout = false;
		blockWaitTimeout = false;
	} 

	/**
	 * Run the server thread
	 * CONCURRENT
	 */
	public void run() // thread run function
	{ 

		while(flagConn) //thread runs while flagConn - Connection is active
		{
			try {

				driver(this.sock.getLocalPort());  // driver to run on the port
			} 

			catch (IOException ex) {
				Logger.getLogger(RTCEServer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		close();

	}       

	//STATEFUL - PART 1
	//A DRIVER RUNS FOR EACH CLIENT.  CONCURRENT
	/**
	 * Run the driver for the client
	 * @param port
	 * @throws IOException
	 */
	void driver(int port) throws IOException 
	{       

		boolean cuauth = false, cack  = false;       // flags for client-cuauth and client-ack
		String curr = "CLOSED";  //initialize current state as closed
		// System.out.println(port);  
		byte[] currRead;

		// loop till you dont get a valid cuauth, if you do then process it
		while(!(curr.matches("CUAUTH")) && !cuauth){ currRead = getRequest(); curr = getName(currRead); process(curr, sock, currRead, log, control, null);} 
		if(curr.matches("CUAUTH"))  //if processed successfully then send connect
		{
			cuauth = true; 
			RTCEServerMessage connectMessage = sauth.getServerMessage();

			if(log.checkBlock(sauth.getClientMessage().getUsername())) //if client is blocked then just close the new connection request
			{
				flagConn = false;
				this.close();
			}

			else //else user is not previously blocked 
			{
				connectMessage.sendMessage(sock, connectMessage.getRequest(), -1, -1);

				//System.out.println("CONNECT Done");

				//while no connect acknowledgement or abort from client
				while(!(curr.matches("CACK")) && !(curr.matches("ABORT")) && !cack){ currRead = getRequest(); curr = getName(currRead); process(curr, sock, currRead, log, control, null);}

				//if connect acknowledged and processed successfully
				if(curr.matches("CACK"))
				{

					cack = true;
					//System.out.println("CACK");

					//if cuauth received and connect acknowledged then add client to active connections and perform session driver
					if(cuauth && cack)
					{  
						sauth.getClientMessage().getUsername();
						RTCEServerLog client = new RTCEServerLog(connectMessage.getSessionId(), sock.getInetAddress(), sauth.getClientMessage().getUsername(), this.sock);                                        
						boolean owner = log.checkOwner();
						log.addActiveConnection(client, port);
						sessionDriver(connectMessage.getSessionId(), port, client, curr, owner);
					}

					//else {}



				}

				//if abort just close the connection
				else if(curr.matches("ABORT")){
					this.close();
				}

			}

		}  
		//if abort just close the connection
		else if(curr.matches("ABORT")){
			this.close();
		}           

	}

	//STATEFUL part 2
	//A SESSION DRIVER RUNS FOR EACH CLIENT SESSION
	/**
	 * Run the server once the session is established
	 * @param session
	 * @param port
	 * @param client
	 * @param curr
	 * @param owner
	 * @throws IOException
	 */
	void sessionDriver(long session, int port, RTCEServerLog client, String curr, boolean owner) throws IOException
	{

		//flags for various states
		boolean slist = false, sdata = false, logoff = false, abort = false;
		byte[] currRead;

		//send document
		this.sendDocument(this.sock, doc1);
		slist = true;
		sdata = true;

		//if slist and sdata are sent then insert the client record in token management - delayed initialization
		if(slist && sdata){

			control.insertClientRecord(client, null);

			//while not logoff or abort, keep the session active
			while(logoff!=true &&  abort!=true){

				currRead = getRequest(); 
				curr = getName(currRead); 

				//if client gets blocked then inform the client that he's blocked and do nothing till unblocked 
				if(client.block == true)
				{
					control.tokenRevoke(client); //revoke any token if client is blocked	
					sendResponse(RTCEMessageType.BLOCK, -1, -1, this.sock); //send block message to client
					startBlockTimer(client);
					while(client.block==true){
						/*if(blockTimeout){
                                    		  client.block = false;
                                    		  cancelBlockTimer();
                                    	  }*/

						try{Thread.sleep(1000);} catch (Exception e){}
					}
					sendResponse(RTCEMessageType.ECHO, -1, -1, sock);
					cancelBlockTimer();
				}

				//if client requests a token then process it (response or denial)
				else if(curr.matches("S_TREQST")){
					// System.out.println("S_TREQST");
					process("S_TREQST", sock, currRead, log, control, client);
					startTokenTimer(client);
				}

				//if client commits then process it (done or no-op)
				else if(curr.matches("S_COMMIT")) {
					process("S_COMMIT", sock, currRead, log, control, client);
					cancelTokenTimer();
					for(Object key : log.connection_list.keySet())
					{
						RTCEServerLog l = (RTCEServerLog)key;
						sendDocument(l.socket,doc1);  
					}

				}

				//if client logs off then delete all resources , stop the thread from running and close this connection
				else if(curr.matches("LOGOFF")){
					logoff = true;
					control.deleteClientRecord(client);
					log.removeActiveConnection(client);
					sendResponse(RTCEMessageType.LACK, -1, -1, this.sock);
					flagConn = false;
					this.close();
					break;
				}

				//if client aborts then do similar as logoff but do not send any acknowledgement to client
				else if(curr.matches("ABORT")){
					abort = true;
					control.deleteClientRecord(client);
					log.removeActiveConnection(client);
					flagConn = false;
					this.close();
					break;
				}

				//if client requests a block to another user then process it-> Only owner can block in implementation
				else if(curr.matches("BLOCK")){
					if(owner)
					{
						process("BLOCK", sock, currRead, log, control, client);
					}
				}




			}


		}
	}



	/**
	 * get request to block till server received next request 
	 * @return the request
	 * @throws IOException
	 */
	byte[] getRequest() throws IOException
	{
		boolean valid = false;
		int dataSize;

		while((dataSize = recvStream.available())==0);

		byte readbf[] = new byte[dataSize];
		recvStream.read(readbf, 0, dataSize);           

		return readbf;


	}

	/**
	 * get the request name 
	 * @param read
	 * @return the request name
	 */
	String getName(byte read[])
	{
		byte asciiVal[] = new byte[8];
		String s;
		RTCEServerMessage clientMessage = new RTCEServerMessage();
		for(int i=0; i<8; i++)
			asciiVal[i] = read[i];

		s = new String(asciiVal, 0, clientMessage.lastByte(asciiVal));
		//System.out.println("INCOMING REQUEST: " + s + "\n");
		return s;      

	}

	/**
	 * allocate resources and buffers of specific pdu size if request is valid and process it
	 * @param s
	 * @param sock
	 * @param read
	 * @param log
	 * @param Control
	 * @param client
	 */
	void process(String s, Socket sock, byte[] read, RTCEServerLog log, RTCEServerRecordMgmt Control, RTCEServerLog client)
	{
		int messageSize;
		RTCEServerMessage clientMessage = new RTCEServerMessage();

		if((messageSize = clientMessage.lengthBuffer(s))!=0){
			ByteBuffer bf = ByteBuffer.allocate(messageSize);
			bf.put(read);
			s = new String(s.getBytes(),getRtcecharset());
			clientMessage.setDocument(doc1);
			clientMessage.recvMessage(sock, RTCEMessageType.valueOf(s), bf, log, Control, client);
			if(clientMessage.getRequest().equals(RTCEMessageType.CUAUTH)){
				sauth = new RTCEServerAuth(clientMessage);
			}
		}

		else{
			//System.out.println("NOT PROCESSED.. \n");
		}

	}

	/**
	 * send the response to the client
	 * @param response
	 * @param token
	 * @param section
	 * @param sock
	 */
	void sendResponse(RTCEMessageType response, double token, int section, Socket sock)
	{
		RTCEServerMessage serverMessage = new RTCEServerMessage();
		serverMessage.sendMessage(sock, response, token, section);

	}

	/**
	 * close a connection 
	 */
	void close()
	{
		try
		{
			recvStream.close();
			sendStream.close();
			sock.close();

		}

		catch(IOException ex)
		{
			System.err.println("IOException in getRequest");
		}

	}

	/**
	 * send a document out on the socket, in implementation now it is the same socket
	 * @param s
	 * @param doc
	 */
	public void sendDocument(Socket s, RTCEDocument doc)
	{
		RTCEServerMessage sMsg = new RTCEServerMessage();
		sMsg.setDocument(doc);
		sMsg.setRequest(RTCEMessageType.S_LIST);
		sMsg.setSessionId(123456); //Need to figure out how to get this value
		sMsg.sendMessage(s,RTCEMessageType.S_LIST, -1, -1);

		try{Thread.sleep(200);} catch (Exception e){}

		doc.resetSectionItr();

		int sID = doc.getNextSectionItr().getID();
		while (sID > 0)
		{
			sMsg.setRequest(RTCEMessageType.S_DATA);    	  
			sMsg.setSectionID(sID);
			sMsg.sendMessage(s,RTCEMessageType.S_DATA, -1, -1);
			try{Thread.sleep(200);} catch (Exception e){}
			sID = doc.getNextSectionItr().getID();
		}  
	}

	/**
	 *  functions for timeouts - tokens
	 * @param client
	 */
	public void startTokenTimer(RTCEServerLog client){
		tokenTimer = new Timer("Token Timeout Timer");
		tokenWaitTimeout = true;
		tokenTimer.schedule(new RTCETokenTimeoutTask(client), RTCEServerConfig.getTokenTime());
	}

	/**
	 *  functions for timeouts - blocks
	 * @param client
	 */
	public void startBlockTimer(RTCEServerLog client){
		blockTimer = new Timer("Block Timeout Timer");
		blockWaitTimeout = true;
		blockTimeout = false;
		blockTimer.schedule(new RTCEBlockTimeoutTask(client), RTCEServerConfig.getBlockTime());
	}

	/**
	 * Are we waiting on a token timeout?
	 * @return true if we are, false otherwise
	 */
	public boolean isTokenWaitTimeout() {
		return tokenWaitTimeout;
	}

	/**
	 * Tell if we are waiting on token timeout
	 * @param tokenWaitTimeout
	 */
	public void setTokenWaitTimeout(boolean tokenWaitTimeout) {
		this.tokenWaitTimeout = tokenWaitTimeout;
	}

	/**
	 * Get the token timer
	 * @return the token timer
	 */
	public Timer getTokenTimer() {
		return tokenTimer;
	}

	/**
	 * Set the token timer
	 * @param tokenTimer
	 */
	public void setTokenTimer(Timer tokenTimer) {
		this.tokenTimer = tokenTimer;
	}

	/**
	 * Is the block timed out?
	 * @return true if yes, false otherwise
	 */
	public boolean isBlockTimeout() {
		return blockTimeout;
	}

	/**
	 * Tell if the block has timed out
	 * @param blockTimeout
	 */
	public void setBlockTimeout(boolean blockTimeout) {
		this.blockTimeout = blockTimeout;
	}

	/**
	 * Are we waiting on a block timeout?
	 * @return true if we are, false otherwise
	 */
	public boolean isBlockWaitTimeout() {
		return blockWaitTimeout;
	}

	/**
	 * Tell if we are waiting on block timeout
	 * @param blockWaitTimeout
	 */
	public void setBlockWaitTimeout(boolean blockWaitTimeout) {
		this.blockWaitTimeout = blockWaitTimeout;
	}

	/**
	 * Get the block timer
	 * @return the block timer
	 */
	public Timer getBlockTimer() {
		return blockTimer;
	}

	/**
	 * Set the block timer
	 * @param blockTimer
	 */
	public void setBlockTimer(Timer blockTimer) {
		this.blockTimer = blockTimer;
	}

	/**
	 * Cancel the token timer
	 */
	public void cancelTokenTimer(){
		tokenTimer.cancel();
		tokenWaitTimeout = false;
		//tokenTimer = null;
	}

	//When token runs out, execute
	public class RTCETokenTimeoutTask extends TimerTask{
		private RTCEServerLog client;
		/**
		 * Create token timeout task
		 * @param client
		 */
		public RTCETokenTimeoutTask(RTCEServerLog client){
			this.client = client;
		}

		@Override
		/**
		 * If waiting for token to expire, revoke it
		 */
		public void run(){
			if(tokenWaitTimeout){
				control.tokenRevoke(client);
				sendResponse(RTCEMessageType.S_REVOKE, -1, -1, sock);
				//tokenTimeout = true;
			}
			tokenWaitTimeout = false;
			tokenTimer.cancel();
			//tokenTimer = null;
		}
	}

	/**
	 * Cancel the block timer
	 */
	public void cancelBlockTimer(){
		blockTimer.cancel();
		blockWaitTimeout = false;
		blockTimeout = false;
		//blockTimer = null;
	}

	//When block ends, execute
	public class RTCEBlockTimeoutTask extends TimerTask{
		private RTCEServerLog client;
		/**
		 * Create block timeout task
		 * @param client
		 */
		public RTCEBlockTimeoutTask(RTCEServerLog client){
			this.client = client;
		}

		@Override
		/**
		 * If waiting for block to expire, end it
		 */
		public void run(){
			if(blockWaitTimeout){
				client.block = false;
				//blockTimeout = true;
			}else{
				//blockTimeout = false;
			}
			blockWaitTimeout = false;
			blockTimer.cancel();
			//blockTimer = null;
		}
	}

	/**
	 * main fucntion of the server
	 * @param arg
	 * @throws IOException
	 */
	public static void main(String arg[]) throws IOException
	{

		//initialize server configuration information
		RTCEServerConfig.init("config/server/servConfig.conf");

		//initialize server log and server record mgmt if Server is active
		RTCEServer server = new RTCEServer(0);

		//Create and start the Discovery Thread
		RTCEDiscoveryServer discServer = new RTCEDiscoveryServer();
		Thread discThread = new Thread(discServer);      
		discThread.start();
		ServerSocket listenSock = new ServerSocket(50000);

		while(true)  // Server runs indefinitely
		{
			//start a new thread by passing in a new socket
			// CONCURRENT: Creating a new thread for each client
			RTCEServer server1 = new RTCEServer(listenSock.accept());          
			Thread thread = new Thread(server1);          
			thread.start();


		}


	}       


}
