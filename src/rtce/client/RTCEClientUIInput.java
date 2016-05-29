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
	   private Scanner sc = new Scanner(System.in);	   
	   private Socket socket;
	   
	   RTCEClientUIInput(Socket s) throws IOException {socket = s;}
	
       public void run()
	   {
    	  String s = null;
    	  
    	  while(true)
    	  {
    		
    		s = sc.next();  
    		System.out.println(s);  
    		
    		//example: login,username,documentname
    		if (s.startsWith("login"))
    		{
    			String[] parts = s.split(",");
    			System.out.println("Logging in as " + parts[1] + " to edit document " + parts[2]);
    			RTCEClientMessage Message = new RTCEClientMessage();
    			Message.setRequest(RTCEMessageType.CUAUTH);
    			Message.setVersion("v1.0".getBytes());;
    			Message.setUsername(parts[1]);
    			Message.setPassword("password");
    			Message.setDocumentTitle(parts[2]);
    			Message.sendMessage(socket, RTCEMessageType.CUAUTH );
    			
    		}
    		else if (s.startsWith("S_") | 
    			     s.startsWith("CUAUTH") |
    			     s.startsWith("ABORT") |
    			     s.startsWith("ECHO") |
    			     s.startsWith("LOGOFF") |
    			     s.startsWith("BLOCK") 
    			    )
    		{
              RTCEClientMessage clientMessage = new RTCEClientMessage();
              clientMessage.sendMessage(socket, RTCEMessageType.valueOf(new String(s.getBytes(), getRtcecharset())));
    			                                      			    		
    		}
    		
    		
    	  }
	   } //run
       
	
	} //RTCEClientUIInput