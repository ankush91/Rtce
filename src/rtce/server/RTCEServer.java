
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static rtce.RTCEConstants.getRtcecharset;
import rtce.RTCEMessageType;
import rtce.RTCEDocument;
import rtce.RTCEMessageType;
import static rtce.server.RTCEServerConfig.getPortNumbers;


public class RTCEServer implements Runnable //THIS CLASS IMPLEMENTS THE MAIN DRIVER FOR ALL THE CLIENT THREADS WITH CONTROL FLOW OF STATEFUL DFA
{
    //Various declairations
    Socket sock; 
    InputStream recvStream;  
    OutputStream sendStream; 
    String request;  
    String response;
    static ServerLog log;
    RTCEServerAuth sauth;
    static RTCEDocument doc1 = new RTCEDocument(1); 
    static ServerRecordMgmt control;
    boolean flagConn;
    private boolean tokenWaitTimeout;
    private Timer tokenTimer;
    private boolean blockTimeout;
    private boolean blockWaitTimeout;
    private Timer blockTimer;
    
    RTCEServer(){}
    
    RTCEServer(int init){
       //initialize the log and client record information for tokens
    	log = new ServerLog();        
        control = new ServerRecordMgmt(); 
    }
    
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
    
    //A DRIVER RUNS FOR EACH CLIENT
    void driver(int port) throws IOException 
    {       
      
      boolean cuauth = false, cack  = false;       // flags for client-cuauth and client-ack
      String curr = "CLOSED";  //initialize current state as closed
      System.out.println(port);  
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
                 connectMessage.sendMessage(sock, RTCEMessageType.CONNECT, -1, -1);
             
                System.out.println("CONNECT Done");
                
             //while no connect acknowledgement or abort from client
                    while(!(curr.matches("CACK")) && !(curr.matches("ABORT")) && !cack){ currRead = getRequest(); curr = getName(currRead); process(curr, sock, currRead, log, control, null);}
                           
                        //if connect acknowledged and processed successfully
                            if(curr.matches("CACK"))
                            {
                                
                                cack = true;
                                System.out.println("CACK");
                                
                                //if cuauth received and connect acknowledged then add client to active connections and perform session driver
                                if(cuauth && cack)
                                        {  
                                         sauth.getClientMessage().getUsername();
                                        ServerLog client = new ServerLog(connectMessage.getSessionId(), sock.getInetAddress(), sauth.getClientMessage().getUsername(), this.sock);                                        
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
    
    //A SESSION DRIVER RUNS FOR EACH CLIENT SESSION
    void sessionDriver(long session, int port, ServerLog client, String curr, boolean owner) throws IOException
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
                                      while(client.block==true){};
                                  }
                                  
                                  //if client requests a token then process it (response or denial)
                                  else if(curr.matches("S_TREQST")){
                                        System.out.println("S_TREQST");
                                        process("S_TREQST", sock, currRead, log, control, client);
                                        startTokenTimer(client);
                                    }
                                    
                                    //if client commits then process it (done or no-op)
                                    else if(curr.matches("S_COMMIT")) {
                                    	process("S_COMMIT", sock, currRead, log, control, client);
                                    	cancelTokenTimer();
                                            for(Object key : log.connection_list.keySet())
                                            {
                                                ServerLog l = (ServerLog)key;
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
    
    
    
   //get request to block till server received next request 
  byte[] getRequest() throws IOException
  {
      boolean valid = false;
      int dataSize;
         
            while((dataSize = recvStream.available())==0);
            
                byte readbf[] = new byte[dataSize];
                recvStream.read(readbf, 0, dataSize);           
           
     return readbf;
      
  
  }
  
  //get the request name 
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
  
  //allocate resources and buffers of specific pdu size if request is valid and process it
  void process(String s, Socket sock, byte[] read, ServerLog log, ServerRecordMgmt Control, ServerLog client)
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
                System.out.println("NOT PROCESSED.. \n");
            }
      
  }
  
  //send the response to the client
  void sendResponse(RTCEMessageType response, double token, int section, Socket sock)
  {
            RTCEServerMessage serverMessage = new RTCEServerMessage();
            serverMessage.sendMessage(sock, response, token, section);
      
  }
  
  //close a connection 
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
  
  //send a document out on the socket, in implementation now it is the same socket
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
  
  // functions for timeouts
	public void startTokenTimer(ServerLog client){
		tokenTimer = new Timer("Token Timeout Timer");
		tokenWaitTimeout = true;
		tokenTimer.schedule(new RTCETokenTimeoutTask(client), RTCEServerConfig.getTokenTime());
	}
	
	public void startBlockTimer(){
		blockTimer = new Timer("Block Timeout Timer");
		blockWaitTimeout = true;
		blockTimeout = false;
		blockTimer.schedule(new RTCEBlockTimeoutTask(), RTCEServerConfig.getBlockTime());
	}

	public boolean isTokenWaitTimeout() {
		return tokenWaitTimeout;
	}

	public void setTokenWaitTimeout(boolean tokenWaitTimeout) {
		this.tokenWaitTimeout = tokenWaitTimeout;
	}

	public Timer getTokenTimer() {
		return tokenTimer;
	}

	public void setTokenTimer(Timer tokenTimer) {
		this.tokenTimer = tokenTimer;
	}

	public boolean isBlockTimeout() {
		return blockTimeout;
	}

	public void setBlockTimeout(boolean blockTimeout) {
		this.blockTimeout = blockTimeout;
	}

	public boolean isBlockWaitTimeout() {
		return blockWaitTimeout;
	}

	public void setBlockWaitTimeout(boolean blockWaitTimeout) {
		this.blockWaitTimeout = blockWaitTimeout;
	}

	public Timer getBlockTimer() {
		return blockTimer;
	}

	public void setBlockTimer(Timer blockTimer) {
		this.blockTimer = blockTimer;
	}

	public void cancelTokenTimer(){
		tokenTimer.cancel();
		tokenWaitTimeout = false;
		//tokenTimer = null;
	}
	
	public class RTCETokenTimeoutTask extends TimerTask{
		private ServerLog client;
		public RTCETokenTimeoutTask(ServerLog client){
			this.client = client;
		}
		
		@Override
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
	
	public void cancelBlockTimer(){
		blockTimer.cancel();
		blockWaitTimeout = false;
		blockTimeout = false;
		blockTimer = null;
	}
	
	public class RTCEBlockTimeoutTask extends TimerTask{
		@Override
		public void run(){
			if(blockWaitTimeout){
				blockTimeout = true;
			}else{
				blockTimeout = false;
			}
			blockWaitTimeout = false;
			blockTimer.cancel();
			blockTimer = null;
		}
	}
  
        //main fucntion of the server
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
      ServerSocket listenSock = new ServerSocket(25351);
     
      while(true)  // Server runs indefinitely
      {
          //start a new thread by passing in a new socket
          
          RTCEServer server1 = new RTCEServer(listenSock.accept());          
          Thread thread = new Thread(server1);          
          thread.start();
          
          
      }
      
      
  }       
  
  
}
