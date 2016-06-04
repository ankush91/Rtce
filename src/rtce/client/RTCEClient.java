
/**
 * @cs544
 * @author GROUP 4 Anthony Emma, Ankush Israney, Edwin Dauber, Francis Obiagwu
 * @version 1
 * @date 6/3/2016
 *  This file is responsible for establishing the client class, performing discovery of the server
 *  and creation of the socket.   Creating threads for the UI, and sending incoming messages to be
 *  processed to other helper routines.
 */

package rtce.client;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

import rtce.RTCEConstants;
import rtce.RTCEDocument;
import rtce.RTCEMessageType;
public class RTCEClient {

	//important objects
	static private Socket sock;
	private OutputStream sendStream;
	private InputStream recvStream;
	private String request, response;
	private RTCEClientConnection cliConn;
	private RTCEClientAuth cAuthModule;
	static private RTCEDocument doc = new RTCEDocument(0);

	//STATEFUL flags
	private boolean cuauth  = false;
	private boolean connect = false;


	//Constants used for Discovery
	//Extra Credit, SERVICE
	final private static int    DISCOVERY_PORT = 4446;
	final private static String MCAST_DISCOVERY_GROUP = "225.0.0.10";

	//Used to alert document after S_DONE from S_COMMIT
	private int commitPrevSectionID = 0;
	private int commitSectionID     = 0;
	private String commitTxt;

	//STATEFUL Token
	private double token = 0;
	private int tokenSection = 0;

	/**
	 * Create a client
	 * @param port - the port number
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	RTCEClient(int port) throws IOException, UnknownHostException
	{

		sock = new Socket (discoverServer(), port);
		sendStream = sock.getOutputStream();
		recvStream = sock.getInputStream();
	}

	/**
	 * make an echo request (unused)
	 */
	void makeRequest()
	{
		//Add code to make request string.
		this.request = "echo";
	}

	/**
	 * Send a generic request
	 * @param request - the request type
	 */
	void sendRequest(RTCEMessageType request)
	{
		RTCEClientMessage clientMessage = new RTCEClientMessage();
		clientMessage.sendMessage(sock, request, -1, -1);
	}

	/**
	 * Get if auth has been sent
	 * @return true if auth sent, false otherwise
	 */
	public boolean isCuauth() {
		return cuauth;
	}

	/**
	 * Set the value of cuauth
	 * @param cuauth - true if sent, false otherwise
	 */
	public void setCuauth(boolean cuauth) {
		this.cuauth = cuauth;
	}

	/**
	 * Get if connect received
	 * @return true if connect received or false otherwise
	 */
	public boolean isConnect() {
		return connect;
	}

	/**
	 * Set the value of connect
	 * @param connect - true if recieved, false otherwise
	 */
	public void setConnect(boolean connect) {
		this.connect = connect;
	}

	/**
	 * Get the token section id
	 * @return the section id the token is for
	 */
	public int getTokenSection() {
		return tokenSection;
	}

	/**
	 * Set the commit previous section id
	 * @param commitPrevSectionID
	 */
	public void setCommitPrevSectionID(int commitPrevSectionID) {
		this.commitPrevSectionID = commitPrevSectionID;
	}

	/**
	 * Set the commit section id - will be same as token section id
	 * @param commitSectionID
	 */
	public void setCommitSectionID(int commitSectionID) {
		this.commitSectionID = commitSectionID;
	}

	/**
	 * Set the commit text
	 * @param commitTxt
	 */
	public void setCommitTxt(String commitTxt) {
		this.commitTxt = commitTxt;
	}

	/**
	 * Set the token value
	 * @param token
	 */
	public void setToken(double token) {
		this.token = token;
	}

	/**
	 * Set the token section
	 * @param tokenSection
	 */
	public void setTokenSection(int tokenSection) {
		this.tokenSection = tokenSection;
	}

	/**
	 * Get the client authentication module
	 * @return the client authentication module
	 */
	public RTCEClientAuth getcAuthModule() {
		return cAuthModule;
	}

	/**
	 * Set the client authentication module
	 * @param cAuthModule
	 */
	public void setcAuthModule(RTCEClientAuth cAuthModule) {
		this.cAuthModule = cAuthModule;
	}

	/**
	 * Get the socket
	 * @return the socket
	 */
	public static Socket getSock() {
		return sock;
	}

	/**
	 * Get the output stream
	 * @return the output stream
	 */
	public OutputStream getSendStream() {
		return sendStream;
	}

	/**
	 * Get the input stream
	 * @return the input stream
	 */
	public InputStream getRecvStream() {
		return recvStream;
	}

	/**
	 * Get the request
	 * @return the request
	 */
	public String getRequest() {
		return request;
	}

	/**
	 * Get the client connection object
	 * @return the client connection object
	 */
	public RTCEClientConnection getCliConn() {
		return cliConn;
	}

	/**
	 * Get the document
	 * @return the document
	 */
	public static RTCEDocument getDoc() {
		return doc;
	}

	/**
	 * Get the discovery port
	 * @return the discovery port
	 */
	public static int getDiscoveryPort() {
		return DISCOVERY_PORT;
	}

	/**
	 * Get the discovery group
	 * @return the discovery group
	 */
	public static String getMcastDiscoveryGroup() {
		return MCAST_DISCOVERY_GROUP;
	}

	/**
	 * Get the commit previous section id
	 * @return the commit previous section id
	 */
	public int getCommitPrevSectionID() {
		return commitPrevSectionID;
	}

	/**
	 * Get the commit section id - same as token section id
	 * @return the commit section id
	 */
	public int getCommitSectionID() {
		return commitSectionID;
	}

	/**
	 * Get the commit text
	 * @return the commit text
	 */
	public String getCommitTxt() {
		return commitTxt;
	}

	/**
	 * Get the token value
	 * @return the token value
	 */
	public double getToken() {
		return token;
	}

	/**
	 * Get and process a message from the server
	 */
	void getResponse()
	{
		try
		{
			int dataSize, messageSize;
			while((dataSize = recvStream.available())==0);

			byte readbf[] = new byte[dataSize];
			recvStream.read(readbf, 0, dataSize);

			byte asciiVal[] = new byte[8];

			for(int i=0; i<8; i++)
				asciiVal[i] = readbf[i];

			RTCEClientMessage serverMessage = new RTCEClientMessage(); 
			String s = new String(asciiVal, 0, serverMessage.lastByte(asciiVal), RTCEConstants.getRtcecharset());
			serverMessage.setDocument(doc);

			if((messageSize = serverMessage.lengthBuffer(s))!=0)
			{   
				ByteBuffer bf = ByteBuffer.allocate(messageSize);            

				bf.put(readbf);
				response = "";
				for(int i = 0; i < s.length(); i++){
					if(s.charAt(i) == 0){
						break;
					}
					response += s.charAt(i);
				}
				//System.out.println("INCOMING RESPONSE:  "+response+"\n");
				serverMessage.recvMessage(sock, RTCEMessageType.valueOf(response), bf);
				if(response.equals("CONNECT")){
					cAuthModule.setServerMessage(serverMessage);
					cliConn = cAuthModule.getConnection();
					cAuthModule.getCack().sendMessage(sock, RTCEMessageType.CACK, -1, -1);
					connect = true;
				}
				if(response.equals("S_DONE") && commitSectionID > 0) {            	
					commitSectionID = 0;
				}
				if(response.equals("S_TRESPN"))
				{
					token = serverMessage.getResponseToken();	                 
				}
				if(response.equals("S_REVOKE"))
				{  token = 0; }
				if(response.equals("BLOCK") )
				{  token = 0; }
				if(response.equals("LACK"))
				{
					System.out.println("Logoff acknowledged by server");
					try{Thread.sleep(2000);} catch (Exception e){}
					System.exit(0);
				}
			}

			else{}

		}

		catch(IOException ex)
		{
			System.err.println("IOException in getRequest");
		}
	}

	/**
	 * Does nothing
	 */
	void useResponse()
	{
		//Add code to use the response string here.
		//System.out.println(response+"\n");
	}

	/**
	 * Close the connection on logout/abort
	 */
	void close()
	{
		try
		{
			sendStream.close();
			recvStream.close();
			sock.close();

		}
		catch(IOException ex)
		{
			System.err.println("IOException in close");
		}
	}       


	/** discoverServer will attempt to locate the IP address of any RTCE server
	 *     if none could be found the loopback adapter 127.0.0.1 will be returned
	 *     EXTRA CREDIT, CLIENT
	 */
	String discoverServer()
	{
		byte[] outboundBuf = new byte[4];

		try{
			//Create the socket and join the Multicast Group for Discovery
			DatagramSocket socket = new DatagramSocket(4447);
			socket.setReuseAddress(true);
			//Create the Datagram with this client's IP Address as the payload
			DatagramPacket outboundPacket;
			System.out.println("Client " + InetAddress.getLocalHost().getHostAddress() + 
					" is searching for a server...");
			outboundBuf = InetAddress.getLocalHost().getAddress();


			for(int i=1; i < 255;i++)
			{   String thisIP = new String(InetAddress.getLocalHost().getHostAddress());
			String[] parts = thisIP.split("\\.");
			String host = parts[0] + "." + parts[1] + "." + parts[2] + "." + i;
			outboundPacket = new DatagramPacket(
					outboundBuf,
					outboundBuf.length,
					InetAddress.getByName(host),4446);
			socket.send(outboundPacket);
			}

			//Prepare to send packet and receive response from server
			int attempts = 0;
			socket.setSoTimeout(1000);     //Give the server 1 second to respond            
			byte[] responseBuf = new byte[2];
			DatagramPacket responsePacket;
			responsePacket = new DatagramPacket(
					responseBuf,
					responseBuf.length);

			//Attempt discovery
			while (attempts < 10)
			{  
				try {            	 
					socket.receive(responsePacket);
					if (responseBuf[0] == 'H' & responseBuf[1] == 'I')
					{ System.out.println("Found Server: " + responsePacket.getAddress());
					socket.close();
					return responsePacket.getAddress().getHostAddress();
					}
				} catch (java.net.SocketTimeoutException e)
				{ attempts++;
				System.out.print(".");
				}                   
			}

			//No server was found
			System.out.println("No servers found.  Enter in server IP");

			socket.close();
			Scanner sc = new Scanner(System.in);
			return sc.nextLine();


		} //try block
		catch (IOException e) {
			e.printStackTrace(); }

		Scanner sc = new Scanner(System.in);
		return sc.nextLine();
	} // end discoverServer()


	/**
	 * Launch the client
	 * SERVICE
	 * CLIENT
	 * @param args - arguments
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		RTCEClientConfig.init("config/client/clientConfig.conf");
		//SERVICE - Hardcoded Port, 50000
		final int servPort = 50000; //Server Port       

		RTCEClient client = new RTCEClient(servPort);

		RTCEClientUIInput UI_Input = new RTCEClientUIInput(sock, client);
		Thread UI_InputThread = new Thread(UI_Input);      
		UI_InputThread.start();

		RTCEClientUIOutput UI_Output = new RTCEClientUIOutput();
		UI_Output.setDocument(doc);
		Thread UI_OutputThread = new Thread(UI_Output);      


		UI_OutputThread.start();
		while (true)
		{
			client.getResponse();        	  
			UI_Output.refreshUI();

		}




	}


}
