
/**
 *
 * @author GROUP4
 * @version 1
 */

package rtce.server;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import static rtce.RTCEConstants.getRtcecharset;
import rtce.RTCEMessageType;
import rtce.RTCEConstants;
import rtce.RTCEDocument;
import rtce.server.clientRecord;
import rtce.RTCEMessageType;



public class RTCEServer implements Runnable
{
    Socket sock;
    InputStream recvStream;
    OutputStream sendStream;
    String request;
    String response;
    ServerLog log;
    RTCEServerAuth sauth;
    RTCEDocument doc1 = new RTCEDocument(1);
    
    RTCEServer (Socket s) throws IOException
    {
        sock = s;
        recvStream = sock.getInputStream();
        sendStream = sock.getOutputStream();
        ServerLog log = new ServerLog();
    } 
    
    public void run()
    { 
      
      boolean flagConn = true;

      int i=0;
      String a[] = {"S_TRESPN", "S_DENIED", "ABORT", "ECHO", "BLOCK", "LACK", "S_REVOKE", "CONNECT"};
      String request;
      while(flagConn)
        {
        request = getRequest();
        if(request.equals("CUAUTH")){
        	sauth.getServerMessage().sendMessage(sock, RTCEMessageType.CONNECT);
        }
        //process(); ***HERE WE HAVE TO CALL THE DRIVER FUNCTION, REMOVE NORMAL REQUEST - RESPONSE FIT INTO DRIVER 

        //sendResponse(RTCEMessageType.valueOf(new String(a[i].getBytes(), getRtcecharset())));
        //i = (i+1)%a.length;
        }
        
        close();
    }       
    
    //ASSUMING A DRIVER RUNS ON EACH PORT FOR EACH CLIENT SESSION -> THINKING OF SOCKET FACTORIES
    void driver(int port) //RUNS EACH CLIENT ON SPECIFIC PORT NUMBER
    {
        /* -----------------------------------PSUEDOCODE MODEL FOR DRIVER-------------------------------------------
      
        
        **********inialize all necessary driver flags****************
        WHEN A NEW DOCUMENT IS CREATED START DATA TIMEOUT ; KEEP IT LONG -> NOT IN THIS FUNCTION THOUGH
        
        WHILE(NOT CUAUTH RECEIVED OR CUAUTH FLAG = FALSE DO NOTHING);
        
                IF CUAUTH RECEIEVED
                                    CHANGE CUAUTH FLAG = TRUE
                                    PROCESS CUAUTH IN SERVER MESSAGE
                                    SEND CONNECT                        
                            
                            WHILE(!(TIMEOUT RECEIVED) OR !(ABORT RECEIVED) OR !(CONNECT RECEIVED) OR CACK FLAG = FALSE DO NOTHING){} //BARRIER BLOCK HERE
                            
                                IF CONNECT RECEIVED 
                                       CACK FLAG = TRUE
                                       IF (CUAUTH FLAG = TRUE, CACK = TRUE)
                                            
                                                SESSION ESTABLISHED CALL DRIVER ON SESSION ->
                                                EXTRACT SESSION INFORMATION
                                                call sessionDriver function
        
                                ELSE
                                    BREAK OUT OF LOOP; //NO CONNECTION ESTABLISHED
        
                                  
                                    
        
                                void sessionDriver(int session number)
        
                                        ***initialize all send flags S_LIST = FLASE, S_DATA = FALSE
                                        ADD CLIENT IN SERVER LOG -> CLIENT IP, PORT NUMBER, SESSION ID    
                                            
                                        SEND S_LIST
                                        CHANGE S_LIST = TRUE 
                                                
                                                IF S_LIST = TRUE
                                                     SEND S_DATA
                                                     
                                                        IF S_LIST = TRUE AND S_DATA = TRUE
                                                        ADD CLIENT IN CLIENT RECORD -> EMPTY TOKEN
        
                                                        WHILE LOGOUT FLAG = FALSE || ABORT FLAG = FALSE || NO TIMEOUT DO THE FOLLOWING
                                                        |    
                                                        |    WHILE(NEW REQUEST NOT RECEIVED DO NOTHING)
                                                        |            START TIMER HERE -> EXCEEDINGLY LONG TIMER A FEW HOURS MAY BE
                                                        |        
                                                        |
                                                        |            IF TOKENREQUEST 
                                                        |               CHECK SECTION FREE USING freeSectionList in ServerRecordMgmt
                                                        |                
                                                        |                IF SECTION FREE && CAN ACCESS SECTION
                                                        |                    GRANT REQUEST 
                                                        |               
                                                        |                 ELSE
                                                        |                     PENDING QUEUE ->SCHEDULING DONE BY PRIORITY IF NEEDED 
                                                        |        
                                                        |            IF COMMIT REQUEST
                                                        |                
                                                        |                 CHECK IF SECTION !FREE && TOKEN FOUND IN CLIENT RECORD
                                                        |                        
                                                        |                        IF RECORD FOUND
                                                        |                        PERFORM COMMIT 
                                                        |                        UPDATE CENTRAL DOCUMENT
                                                        |                       SEND S_DONE
                                                        |
                                                        |                       ELSE
                                                        |                        SEND S_REVOKE
                                                        |
                                                        |             IF LOGOFF
                                                        |                SEND LACK;  BREAK FROM HERE; LOGOUT FLAG = TRUE
                                                        |
                                                        |             IF ABORT 
                                                        |                ABORT FLAG = TRUE; BREAK FROM HERE
                                                        |                       
                                                        |
                                                        |             IF BLOCK FLAG = ON 
                                                        |               SEND BLOCK INFORMATION; BLOCK FLAG = TRUE
                                                        |            
                                                        |           
                                                        |            
        
        DRIVER FOR DATA UPDATE
        
        void dataUpdate()
        
        IF DATA TIMEOUT
                SEND UPDATED DATA TO ALL ACTIVE CLIENT CONNECTIONS FOUND IN LOG                                                          
                                                     
     ************************************************************************************************************************************   
         
        
        */
      
      boolean cuauth = false, cack  = false;
      String curr = null;
      
     while(! curr.matches("CUAUTH") ||!cuauth){ curr = getRequest();} 
            
             if(curr.matches("CUAUTH"))
             {
             cuauth = true;
             RTCEServerMessage connectMessage = sauth.getServerMessage();
             connectMessage.sendMessage(sock, RTCEMessageType.CONNECT);
             //sendResponse(RTCEMessageType.valueOf(new String("CONNECT".getBytes(), getRtcecharset())));
             
                
                    while(!(curr.matches("CACK")) || !(curr.matches("ABORT")) || !cack){ curr = getRequest();}
                                
                            if(curr.matches("CACK"))
                            {
                                cack = true;
                                
                                if(cuauth && cack)
                                        {
                                        //ServerLog client = new ServerLog(12345, );
                                        //log.addActiveConnection(client, port);
                                        //sessionDriver(session, port);
                                        }
                                
                                else {}
                                    
                                
                                
                            }
            
                            else {}
                                    
                    
                    
             }  
             
             else  {}
             
                    
                    
    }
    
    void sessionDriver(int session, int port)
    {
        /*
        ***initialize all send flags S_LIST = FLASE, S_DATA = FALSE
                                        ADD CLIENT IN SERVER LOG -> CLIENT IP, PORT NUMBER, SESSION ID    
                                            
                                        SEND S_LIST
                                        CHANGE S_LIST = TRUE 
                                                
                                                IF S_LIST = TRUE
                                                     SEND S_DATA
                                                     
                                                        IF S_LIST = TRUE AND S_DATA = TRUE
                                                        ADD CLIENT IN CLIENT RECORD -> EMPTY TOKEN
        
                                                        WHILE LOGOUT FLAG = FALSE || ABORT FLAG = FALSE || NO TIMEOUT DO THE FOLLOWING
                                                        |    
                                                        |    WHILE(NEW REQUEST NOT RECEIVED DO NOTHING)
                                                        |            START TIMER HERE -> EXCEEDINGLY LONG TIMER A FEW HOURS MAY BE
                                                        |        
                                                        |
                                                        |            IF TOKENREQUEST 
                                                        |               CHECK SECTION FREE USING freeSectionList in ServerRecordMgmt
                                                        |                
                                                        |                IF SECTION FREE && CAN ACCESS SECTION
                                                        |                    GRANT REQUEST 
                                                        |               
                                                        |                 ELSE
                                                        |                     PENDING QUEUE ->SCHEDULING DONE BY PRIORITY IF NEEDED 
                                                        |        
                                                        |            IF COMMIT REQUEST
                                                        |                
                                                        |                 CHECK IF SECTION !FREE && TOKEN FOUND IN CLIENT RECORD
                                                        |                        
                                                        |                        IF RECORD FOUND
                                                        |                        PERFORM COMMIT 
                                                        |                        UPDATE CENTRAL DOCUMENT
                                                        |                       SEND S_DONE
                                                        |
                                                        |                       ELSE
                                                        |                        SEND S_REVOKE
                                                        |
                                                        |             IF LOGOFF
                                                        |                SEND LACK;  BREAK FROM HERE; LOGOUT FLAG = TRUE
                                                        |
                                                        |             IF ABORT 
                                                        |                ABORT FLAG = TRUE; BREAK FROM HERE
                                                        |                       
                                                        |
                                                        |             IF BLOCK FLAG = ON 
                                                        |               SEND BLOCK INFORMATION; BLOCK FLAG = TRUE
                                                        |            
                                                        |           
                                                        |            
        
        */
        
        boolean slist = false, sdata = false;
        
        log.addActiveConnection(new ServerLog(session, null), port);
            
            sendResponse(RTCEMessageType.valueOf(new String("S_LIST".getBytes(), getRtcecharset())));
            slist = true;
            
                if(slist){
                    sendResponse(RTCEMessageType.valueOf(new String("S_DATA".getBytes(), getRtcecharset())));
                        
                        //if(slist && sdata)
                               
            
              }
    }
    
    
    
  String getRequest()
  {
      boolean valid = false;
      
      try
      {
          int dataSize, messageSize;
          
          
          while((dataSize = recvStream.available())==0);
          
          byte readbf[] = new byte[dataSize];
          recvStream.read(readbf, 0, dataSize);
          
          byte asciiVal[] = new byte[8];
          
          for(int i=0; i<8; i++)
              asciiVal[i] = readbf[i];
          
          RTCEServerMessage clientMessage = new RTCEServerMessage(); 
           String s = new String(asciiVal, 0, clientMessage.lastByte(asciiVal), RTCEConstants.getRtcecharset());
         
         
          if((messageSize = clientMessage.lengthBuffer(s))!=0)
          {   
            ByteBuffer bf = ByteBuffer.allocate(messageSize);
            
            bf.put(readbf);
            String requestTemp = new String(s.getBytes(),getRtcecharset());
            request = "";
            for(int i = 0; i < requestTemp.length(); i++){
            	if(requestTemp.charAt(i) == 0){
            		break;
            	}else{
            		request += requestTemp.charAt(i);
            	}
            }
            valid = true;
            System.out.println(s + " : " + requestTemp + " : " + request);
            System.out.println("INCOMING REQUEST: " + request+ "\n");
          
            clientMessage.recvMessage(sock, RTCEMessageType.valueOf(request), bf);
            if(request.equals("CUAUTH")){
            	sauth = new RTCEServerAuth(clientMessage);
            }
          }
          
          else  
                {
              System.out.println("NOT PROCESSED.. \n");
                }
          
      }
      
      catch(IOException ex)
      {
          System.err.println("IOException in getRequest");
      }
      
      
      if(valid)
          return request;
      else
        return null;
  }
  
  
  void sendResponse(RTCEMessageType response)
  {
            RTCEServerMessage serverMessage = new RTCEServerMessage();
            serverMessage.sendMessage(sock, response);
      
  }
  
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
  
  public void sendDocument(Socket s, RTCEDocument doc)
  {
      RTCEServerMessage sMsg = new RTCEServerMessage();
      sMsg.setDocument(doc);
      sMsg.setRequest(RTCEMessageType.S_LIST);
      sMsg.setSessionId(123456); //Need to figure out how to get this value
      sMsg.sendMessage(s,RTCEMessageType.S_LIST);
	  
      try{Thread.sleep(200);} catch (Exception e){}
	  
      doc.resetSectionItr();
      
      int sID = doc.getNextSectionItr().ID;
      while (sID > 0)
      {
         sMsg.setRequest(RTCEMessageType.S_DATA);    	  
         sMsg.setSectionID(sID);
         sMsg.sendMessage(s,RTCEMessageType.S_DATA);
         try{Thread.sleep(200);} catch (Exception e){}
         sID = doc.getNextSectionItr().ID;
      }  
  }
  
  public static void main(String arg[]) throws IOException
  {
	  RTCEServerConfig.init("config/server/servConfig.conf");
      final int port = 25351; //Provide Server Port
      ServerSocket listenSock = new ServerSocket(port);
      
      //Create and start the Discovery Thread
      RTCEDiscoveryServer discServer = new RTCEDiscoveryServer();
      Thread discThread = new Thread(discServer);      
      discThread.start();
      
      while(true)
      {
          RTCEServer server = new RTCEServer(listenSock.accept());          
          Thread thread = new Thread(server);          
          thread.start();
          
      }
  }       
  
  
}
