
/**
 *
 * @author GROUP4
 * @version 1
 */

package rtce.server;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static rtce.RTCEConstants.getRtcecharset;
import rtce.RTCEMessageType;
import rtce.RTCEDocument;
import rtce.RTCEMessageType;
import static rtce.server.RTCEServerConfig.getPortNumbers;


public class RTCEServer implements Runnable
{
    Socket sock;
    InputStream recvStream;
    OutputStream sendStream;
    String request;
    String response;
    static ArrayList Ports;
    ServerLog log;
    RTCEServerAuth sauth;
    RTCEDocument doc1 = new RTCEDocument(1);
    ServerRecordMgmt control;
    
    RTCEServer(){}
    
    RTCEServer (Socket s) throws IOException
    {
        sock = s;
        recvStream = sock.getInputStream();
        sendStream = sock.getOutputStream();
        //Ports = getPortNumbers();
        log = new ServerLog();        
        control = new ServerRecordMgmt();
    } 
    
    public void run()
    { 
      
      boolean flagConn = true;

      while(flagConn)
        {
          try {
              
              driver(this.sock.getLocalPort());
              
          } 
          
          catch (IOException ex) {
              Logger.getLogger(RTCEServer.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
        
        close();
        
    }       
    
    //ASSUMING A DRIVER RUNS ON EACH PORT FOR EACH CLIENT SESSION -> THINKING OF SOCKET FACTORIES
    void driver(int port) throws IOException //RUNS EACH CLIENT ON SPECIFIC PORT NUMBER
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
      String curr = "CLOSED";
      System.out.println(port);
      byte[] currRead;
     while(!(curr.matches("CUAUTH")) && !cuauth){ currRead = getRequest(); curr = getName(currRead); process(curr, sock, currRead);} 
             if(curr.matches("CUAUTH"))
             {
             cuauth = true; 
             RTCEServerMessage connectMessage = sauth.getServerMessage();
             connectMessage.sendMessage(sock, RTCEMessageType.CONNECT, -1, -1);
             
             System.out.println("CONNECT Done");
                
                    while(!(curr.matches("CACK")) && !(curr.matches("ABORT")) && !cack){ currRead = getRequest(); curr = getName(currRead); process(curr, sock, currRead);}
                                
                            if(curr.matches("CACK"))
                            {
                                
                                cack = true;
                                System.out.println("CACK");
                                
                                if(cuauth && cack)
                                        {
                                        
                                        ServerLog client = new ServerLog(connectMessage.getSessionId(), sock.getInetAddress());                                        
                                        log.addActiveConnection(client, port);
                                        sessionDriver(connectMessage.getSessionId(), port, client, curr);
                                        }
                                
                                else {}
                                    
                                
                                
                            }
            
                            else {}
                                    
                    
                    
             }  
             
             else  {}
             
                    
                    
    }
    
    void sessionDriver(long session, int port, ServerLog client, String curr) throws IOException
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
        
        boolean slist = false, sdata = false, logoff = false, abort = false;
        byte[] currRead;
                    this.sendDocument(this.sock, doc1);
                        slist = true;
                        sdata = true;
                    
                        if(slist && sdata){
                            
                         control.insertClientRecord(client, null);
                         
                                while(!(logoff) || !(abort)){
                                    
                                  currRead = getRequest(); 
                                  curr = getName(currRead); 
                                  //process(curr, sock, currRead);
                                    
                                    if(curr.matches("STREQST")){
                                        System.out.println("STREQST");
                                 //               process("STREQST", sock);
                                     
                                        /*
                                        else 
                                                control.queueAdd();
                                                //send S_DENIED
                                        
                                        */
                                    }
                                    
                                    if(curr.matches("S_COMMIT")){
                                        //execute action
                                        //S_DONE
                                        //else
                                        //S_REVOKE ->remove all client info
                                        
                                    }
                                    
                                    if(curr.matches("S_LOGOFF")){
                                        logoff = true;
                                            control.deleteClientRecord(client);
                                            log.removeActiveConnection(port);
                                            
                                            sendResponse(RTCEMessageType.LACK, -1, -1, this.sock);
                                        break;
                                    }
                                    
                                    if(curr.matches("ABORT")){
                                        abort = true;
                                            control.deleteClientRecord(client);
                                            log.removeActiveConnection(port);
                                        break;
                                    }
                                     
                                    /*if()
                                
                                    */
                                
                                
                                
                                
                                
                                
                                
                                
                                
                                       
                                
                                
                                
                               
                        }
                         
            
              }
    }
    
    
    
    
  byte[] getRequest() throws IOException
  {
      boolean valid = false;
      int dataSize;
         
            while((dataSize = recvStream.available())==0);
            
                byte readbf[] = new byte[dataSize];
                recvStream.read(readbf, 0, dataSize);           
           
     return readbf;
      
  
  }
  
  String getName(byte read[])
  {
       byte asciiVal[] = new byte[8];
       String s;
       RTCEServerMessage clientMessage = new RTCEServerMessage();
      for(int i=0; i<8; i++)
                    asciiVal[i] = read[i];
              
                s = new String(asciiVal, 0, clientMessage.lastByte(asciiVal));
                System.out.println("INCOMING REQUEST: " + s + "\n");
          return s;      
               
  }
  
  
  void process(String s, Socket sock, byte[] read)
  {
      int messageSize;
      RTCEServerMessage clientMessage = new RTCEServerMessage();
      
            if((messageSize = clientMessage.lengthBuffer(s))!=0){
                ByteBuffer bf = ByteBuffer.allocate(messageSize);
                bf.put(read);
                s = new String(s.getBytes(),getRtcecharset());
                clientMessage.recvMessage(sock, RTCEMessageType.valueOf(s), bf);
                if(clientMessage.getRequest().equals(RTCEMessageType.CUAUTH)){
                	sauth = new RTCEServerAuth(clientMessage);
                }
            }
      
            else{
                System.out.println("NOT PROCESSED.. \n");
            }
      
  }
  
  void sendResponse(RTCEMessageType response, double token, int section, Socket sock)
  {
            RTCEServerMessage serverMessage = new RTCEServerMessage();
            serverMessage.sendMessage(sock, response, token, section);
      
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
      sMsg.sendMessage(s,RTCEMessageType.S_LIST, -1, -1);
	  
      try{Thread.sleep(200);} catch (Exception e){}
	  
      doc.resetSectionItr();
      
      int sID = doc.getNextSectionItr().ID;
      while (sID > 0)
      {
         sMsg.setRequest(RTCEMessageType.S_DATA);    	  
         sMsg.setSectionID(sID);
         sMsg.sendMessage(s,RTCEMessageType.S_DATA, -1, -1);
         try{Thread.sleep(200);} catch (Exception e){}
         sID = doc.getNextSectionItr().ID;
      }  
  }
  
  /*public static ServerSocket create(ArrayList<Integer> ports) throws IOException {
      
      for (int port : ports) 
    {
        try {
            System.out.println(port);
            return new ServerSocket(port); //try if this port works
        } catch (IOException ex) {
            continue; // try next port
        }
    }

    // if the program gets here, no port in the range was found free
    throw new IOException("NO PORT IS FREE RIGHT NOW");
}*/
  
  public static void main(String arg[]) throws IOException
  {
      
      RTCEServerConfig.init("config/server/servConfig.conf");
      
      //Create and start the Discovery Thread
      RTCEDiscoveryServer discServer = new RTCEDiscoveryServer();
      Thread discThread = new Thread(discServer);      
      discThread.start();
      ServerSocket listenSock = new ServerSocket(25351);
     // ServerSocket listenSock2 = new ServerSocket(50000);
      
      while(true)
      {
           //create a new socket here which is free
          
          RTCEServer server1 = new RTCEServer(listenSock.accept());          
          Thread thread = new Thread(server1);          
          thread.start();
          
          
      }
      
      
  }       
  
  
}
