/**
 * @cs544
 * @author GROUP 4 Anthony Emma, Ankush Israney, Edwin Dauber, Francis Obiagwu
 * @version 1
 * @date 6/3/2016
 * 	  This file is responsible for establishing the input portion of the UI and handle commands from
 *    user using the PDU.   This file contains some of the statefulness restrictions on the client side
 *    to ensure the DFA is adhered to.
 */

package rtce.client;

import java.net.*;
import java.io.*;
import java.io.IOException;
import java.util.Scanner;
import rtce.RTCEMessageType;
import rtce.RTCEConstants;
import static rtce.RTCEConstants.getRtcecharset;

public class RTCEClientUIInput implements Runnable
{
	//UI objects
	private Scanner sc = new Scanner(System.in);	   
	private Socket socket;
	private RTCEClient parent;

	/**
	 * Create a UI Input object
	 * @param s - socket
	 * @param c - client
	 * @throws IOException
	 */
	RTCEClientUIInput(Socket s, RTCEClient c) throws IOException {
		socket = s;
		parent = c;
	}

	//STATEFUL
	//UI
	/**
	 * Run the ui thread
	 */
	public void run()
	{
		String s = null;

		while(true)
		{
			s = sc.nextLine();
			//s = sc.next();      		 

			//User wants to login 
			//example: login,username,password,documentowner,documentname
			if (s.startsWith("login"))
			{   if (parent.isConnect() == false)
			{
				String[] parts = s.split(",");
				if (parts.length != 5)
				{ System.out.println("INVALID: Invalid login format (ex: login,cs544,cs544,cs544,example1"); }
				else
				{
					System.out.println("Logging in as " + parts[1] + " to edit document " + parts[3] + "/" + parts[4]);
					parent.setcAuthModule(new RTCEClientAuth(parts[1], parts[2], parts[3], parts[4]));
					parent.getcAuthModule().getClientMessage().sendMessage(socket, RTCEMessageType.CUAUTH, -1, -1);
					parent.setCuauth(true);
				}
			}
			else
			{	System.out.println("INVALID: Already logged in");	    }
			}

			//change section text
			//example:   commit,previous section ID,changed section ID
			//example:   commit,1,2
			else if (s.startsWith("commit"))
			{
				if (parent.isConnect() == true && parent.getToken() > 0)
				{
					String[] parts = s.split(",");
					System.out.println("Enter new text");
					String newText = sc.nextLine();

					if (parts.length != 3)
					{
						System.out.println("INVALID: Invalid format for commit (ex. commit,1,2)"); 
					}
					else
					{
						parent.setCommitPrevSectionID(Integer.parseInt(parts[1]));
						parent.setCommitSectionID(Integer.parseInt(parts[2]));
						parent.setCommitTxt(newText);

						if (parent.getCommitSectionID() != parent.getTokenSection())
						{  System.out.println("INVALID: Token is only for section " + parent.getTokenSection()); }
						else
						{
							RTCEClientMessage Message = new RTCEClientMessage();
							Message.setRequest(RTCEMessageType.S_COMMIT);
							Message.setCommitData(
									parent.getToken(),  
									parent.getCommitPrevSectionID(), 
									parent.getCommitSectionID(), 
									parent.getCommitTxt());
							Message.setSessionId(parent.getCliConn().getSessionId());
							Message.sendMessage(socket, RTCEMessageType.S_COMMIT, -1, -1);
							parent.setToken(0);
							parent.setCommitSectionID(0);
						}
					}
				}
				else
				{
					if (parent.isConnect() == false) {System.out.println("INVALID: Not logged in");}
					if (parent.getToken()   == 0)     {System.out.println("INVALID: No token aquired");}    			      				
				}

			} //commit

			//test for Request message 
			else if(s.startsWith("request"))
			{
				if (parent.isConnect() == true)	
				{
					if (parent.getToken() == 0)
					{
						RTCEClientMessage clientMessage = new RTCEClientMessage();
						String[] parts = s.split(",");

						int a = Integer.parseInt(parts[1]); 
						System.out.println("Requested for: "+a);
						clientMessage.setSectionId(a);
						parent.setTokenSection(a);
						clientMessage.setSessionId(parent.getCliConn().getSessionId());
						String reqst = "S_TREQST";
						clientMessage.sendMessage(socket, RTCEMessageType.valueOf(new String(reqst.getBytes(), getRtcecharset())), -1, a);
					}
					else
					{
						System.out.println("Cannot request another token.  Client already has token for Section " + parent.getTokenSection());	             	 
					}
				}
				else
				{
					System.out.println("INVALID: Not Logged in");              	  
				}
			}
			else if (s.startsWith("block"))
			{
				if (parent.isConnect() == true)	
				{  String[] parts = s.split(",");
				RTCEClientMessage Message = new RTCEClientMessage();
				Message.setRequest(RTCEMessageType.BLOCK);
				Message.setUsername(parts[1]);
				boolean[] flags = new boolean[4*8];
				Message.setFlags(flags);
				Message.setSessionId(parent.getCliConn().getSessionId());
				Message.sendMessage(socket, RTCEMessageType.BLOCK, -1, -1);            	
				}            
			}
			//User just wants to send out a test message in PDU
			else if (s.startsWith("S_") | 
					s.startsWith("CUAUTH") |
					s.startsWith("ABORT") |
					s.startsWith("ECHO") |
					s.startsWith("LOGOFF") |
					s.startsWith("BLOCK") |
					s.startsWith("CACK") 
					)
			{
				if (parent.isConnect() == true)
				{
					RTCEClientMessage clientMessage = new RTCEClientMessage();
					clientMessage.sendMessage(socket, RTCEMessageType.valueOf(new String(s.getBytes(), getRtcecharset())), -1, -1);
				}
				else
				{
					System.out.println("INVALID: Not Logged in");
				}

				if (s.startsWith("ABORT"))
				{
					parent.setConnect(false);
					parent.setCuauth(false);
					System.exit(0);
				}

			} //PDU commands






		}
	} //run


} //RTCEClientUIInput
