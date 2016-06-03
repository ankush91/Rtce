/**

 * @cs544
 * @author GROUP 4 Ankush Israney, Edwin Dauber, Anthony Emma, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 * 
 * RTCEServerLog - This class contains a log of all active client connection objects on the server with their port numbers. The session Id, username,
 * ip address, socket for the thread and block field are stored for a connection in the log. The function to block a client is also
 * a part of this class.
 */

package rtce.server;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class RTCEServerLog
{
	static HashMap connection_list; 
	private InetAddress clientIp;
	private int clientPort;
	private long sessionId;
	private String userName;
	boolean block;
	Socket socket;

	/**
	 * Create connection list
	 */
	RTCEServerLog() //LOG OF ACTIVE CONNECTIONS
	{
		connection_list = new HashMap<Integer, RTCEServerLog>(); //initialize Hashmap from port -> client session
	}

	/**
	 * Create actual log entry
	 * @param sessionId
	 * @param ip
	 * @param user
	 * @param sock
	 */
	RTCEServerLog(long sessionId, InetAddress ip, String user, Socket sock) //initalize an inidividual client in Server Log
	{
		this.sessionId = sessionId;
		clientIp = ip;
		userName = user;
		block = false;
		socket = sock;
	}

	/**
	 * Set the bloc
	 * @param username
	 * @return true if blocked, false otherwise
	 */
	public boolean setBlock(String username) //FUNCTION TO BLOCK A USER
	{
		RTCEServerLog l;
		boolean debug=false;
		for (Object key : connection_list.keySet()) {
			l = (RTCEServerLog)key;
			if(l.userName.matches(username)){
				l.block = true;
				debug = true;
				return true;
			}
		}
		if(debug==false){}
		//   System.out.println("not found");
		return false;

	}     

	/**
	 * Find blocked client
	 * @param username
	 * @return entry corresponding to blocked client
	 */
	public RTCEServerLog getBlockedClientId(String username) //Funtion to get Blocked client Id by username
	{
		RTCEServerLog l;
		boolean debug=false;
		for (Object key : connection_list.keySet()) {
			l = (RTCEServerLog)key;
			if(l.userName.matches(username)){
				l.block = true;
				debug = true;
				return l;
			}
		}
		if(debug==false){}
		//   System.out.println("not found");
		return null;

	}     

	/**
	 * Check if user is blocked
	 * @param username 
	 * @return true if blocked, false otherwise
	 */
	public boolean checkBlock(String username) //Function to check if a user is blocked
	{
		RTCEServerLog l;
		boolean debug=false;
		for (Object key : connection_list.keySet()) {
			l = (RTCEServerLog)key;
			if(l.userName.matches(username)){
				l.block = true;
				debug = true;
				return true;
			}
		}
		if(debug==false){}
		//   System.out.println("not found");
		return false;

	}   

	/**
	 * Get block for given client
	 * @param client
	 * @return true if blocked, false otherwise
	 */
	public boolean getBlock(RTCEServerLog client) //return the block flag of a user
	{
		return client.block;
	}

	/**
	 * Add an active connection
	 * @param clientObject
	 * @param port
	 */
	public void addActiveConnection(RTCEServerLog clientObject, int port) //ADD CONNECTION - PORT MAPPING IN LOG
	{
		connection_list.put(clientObject, port);
	}

	/**
	 * Check for a connection for a client
	 * @param clientObject
	 * @return true if not connected, false otherwise
	 */
	public boolean checkConnection(RTCEServerLog clientObject) //check for connection on client in log
	{
		RTCEServerLog status = (RTCEServerLog) connection_list.get(clientObject); 
		if(status == null)
		{
			return true;
		}

		return false;

	} 

	/**
	 * Get the session id
	 * @return the session id
	 */
	public long getSessionId() //RETURN SESSION ID OF ACTIVE CONNECTION
	{
		return this.sessionId;
	}

	/**
	 * Remove a connection
	 * @param clientObject
	 */
	public void removeActiveConnection(RTCEServerLog clientObject) //REMOVE FROM LIST OF ACTIVE CONNECTIONS
	{
		connection_list.remove(clientObject);
	}

	/**
	 * Check if new connection is the owner of the document
	 * @return true if first connection, false otherwise
	 */
	public boolean checkOwner() //CHECK OWNER FOR DOCUMENT
	{
		if(connection_list.isEmpty())
			return true;
		else
			return false;
	}

	/**
	 * List all of the currently used session ids
	 * @return the list of currently used session ids
	 */
	public static ArrayList<Long> usedSessionIds(){
		RTCEServerLog l;
		ArrayList<Long> sessionList = new ArrayList<Long>();
		for(Object key : connection_list.keySet()){
			l = (RTCEServerLog)key;
			sessionList.add(l.getSessionId());
		}
		return sessionList;
	}


}
