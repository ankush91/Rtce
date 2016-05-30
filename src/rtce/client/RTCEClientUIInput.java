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
	   private RTCEClient parent;
	   
	   RTCEClientUIInput(Socket s, RTCEClient c) throws IOException {
		   socket = s;
		   parent = c;
	   }
	
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
    		{
    			String[] parts = s.split(",");
    			System.out.println("Logging in as " + parts[1] + " to edit document " + parts[3] + "/" + parts[4]);
    			parent.setcAuthModule(new RTCEClientAuth(parts[1], parts[2], parts[3], parts[4]));
    			parent.getcAuthModule().getClientMessage().sendMessage(socket, RTCEMessageType.CUAUTH, -1, -1);
    			/*RTCEClientMessage Message = new RTCEClientMessage();
    			Message.setRequest(RTCEMessageType.CUAUTH);
    			Message.setVersion("v1.0".getBytes());;
    			Message.setUsername(parts[1]);
    			Message.setPassword("password");
    			Message.setDocumentTitle(parts[2]);
    			Message.sendMessage(socket, RTCEMessageType.CUAUTH );*/
    			
    		}
    		
    		//change section text
    		//example:   commit,previous section ID,changed section ID
    		//example:   commit,1,2
    		else if (s.startsWith("commit"))
    		{
    			String[] parts = s.split(",");
    			System.out.println("Enter new text");
    			String newText = sc.nextLine();
    			
    			parent.commitPrevSectionID = Integer.parseInt(parts[1]);
    			parent.commitSectionID     = Integer.parseInt(parts[2]);
    			parent.commitTxt           = newText;
    			
    			RTCEClientMessage Message = new RTCEClientMessage();
    			Message.setRequest(RTCEMessageType.S_COMMIT);
    			Message.setCommitData(
    					123456, //need to hook to Ankush's token 
    					parent.commitPrevSectionID, 
    					parent.commitSectionID, 
    					parent.commitTxt);
    			Message.setSessionId(parent.getcAuthModule().getConnection().getSessionId());
    			Message.sendMessage(socket, RTCEMessageType.S_COMMIT, -1, -1);
    			 
    		}
                
                //test for Request message 
                 else if(s.startsWith("S_TREQST"))
                {
                     RTCEClientMessage clientMessage = new RTCEClientMessage();
                     
                     int a = Integer.parseInt(s.replaceAll("[^0-9]", "")); 
                     System.out.println(a);
                     clientMessage.setSectionId(a);
                     
                     String reqst = "S_TREQST";
                     clientMessage.sendMessage(socket, RTCEMessageType.valueOf(new String(reqst.getBytes(), getRtcecharset())), -1, a);
                     
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
              RTCEClientMessage clientMessage = new RTCEClientMessage();
              clientMessage.sendMessage(socket, RTCEMessageType.valueOf(new String(s.getBytes(), getRtcecharset())), -1, -1);
    			                                      			    		
    		}
                
               
                
      
    		
    		
    	  }
	   } //run
       
	
	} //RTCEClientUIInput