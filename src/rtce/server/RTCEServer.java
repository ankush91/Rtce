
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


public class RTCEServer implements Runnable
{
    Socket sock;
    InputStream recvStream;
    OutputStream sendStream;
    String request;
    String response;
    static ArrayList Ports;
    static ServerLog log;
    RTCEServerAuth sauth;
    static RTCEDocument doc1 = new RTCEDocument(1);
    static ServerRecordMgmt control;
    boolean flagConn;
    private boolean timeout;
    private boolean waitTimeout;
    private Timer timer;
    
    RTCEServer(){
    	log = new ServerLog();        
        control = new ServerRecordMgmt();
    }
    
    RTCEServer (Socket s) throws IOException
    {
        sock = s;
        recvStream = sock.getInputStream();
        sendStream = sock.getOutputStream();
        //Ports = getPortNumbers();
        
        flagConn = true;
        timeout = false;
        waitTimeout = false;
    } 
    
    public void run()
    { 

      while(flagConn)
        {
          try {
              //boolean ownerFlag = log.checkOwner();
              //driver(this.sock.getLocalPort(), ownerFlag);
              driver(this.sock.getLocalPort());
          } 
          
          catch (IOException ex) {
              Logger.getLogger(RTCEServer.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
        
        close();
        
    }       
    
    //ASSUMING A DRIVER RUNS ON EACH PORT FOR EACH CLIENT SESSION 
    void driver(int port) throws IOException //RUNS EACH CLIENT ON SPECIFIC PORT NUMBER
    {       
      
      boolean cuauth = false, cack  = false;
      String curr = "CLOSED";
      System.out.println(port);
      byte[] currRead;
     while(!(curr.matches("CUAUTH")) && !cuauth){ currRead = getRequest(); curr = getName(currRead); process(curr, sock, currRead, log, control, null);} 
             if(curr.matches("CUAUTH"))
             {
             cuauth = true; 
             RTCEServerMessage connectMessage = sauth.getServerMessage();
             connectMessage.sendMessage(sock, RTCEMessageType.CONNECT, -1, -1);
             
             System.out.println("CONNECT Done");
                
                    while(!(curr.matches("CACK")) && !(curr.matches("ABORT")) && !cack){ currRead = getRequest(); curr = getName(currRead); process(curr, sock, currRead, log, control, null);}
                                
                            if(curr.matches("CACK"))
                            {
                                
                                cack = true;
                                System.out.println("CACK");
                                
                                if(cuauth && cack)
                                        {  
                                         sauth.getClientMessage().getUsername();
                                        ServerLog client = new ServerLog(connectMessage.getSessionId(), sock.getInetAddress(), sauth.getClientMessage().getUsername());                                        
                                        log.addActiveConnection(client, port);
                                        boolean owner = log.checkOwner();
                                        sessionDriver(connectMessage.getSessionId(), port, client, curr, owner);
                                        }
                                
                                else {}
                                    
                                
                                
                            }
            
                            else if(curr.matches("ABORT")){
                                this.close();
                            }
                                    
                    
                    
             }  
             
                else if(curr.matches("ABORT")){
                                this.close();
                            }           
                    
    }
    
    void sessionDriver(long session, int port, ServerLog client, String curr, boolean owner) throws IOException
    {
       
        
        boolean slist = false, sdata = false, logoff = false, abort = false;
        byte[] currRead;
                    this.sendDocument(this.sock, doc1);
                        slist = true;
                        sdata = true;
                    
                        if(slist && sdata){
                            
                         control.insertClientRecord(client, null);
                         
                                while(logoff!=true &&  abort!=true){
                                    
                                  currRead = getRequest(); 
                                  curr = getName(currRead); 
                                  //process(curr, sock, currRead);
                                    
                                  if(client.block == true)
                                  {
                                      while(client.block==true){};
                                  }
                                  
                                  else if(curr.matches("S_TREQST")){
                                        System.out.println("S_TREQST");
                                        process("S_TREQST", sock, currRead, log, control, client);
                                     
                                    }
                                    
                                    else if(curr.matches("S_DONE")){
                                        process("S_COMMIT", sock, currRead, log, control, client);
                                    }
                                    
                                    else if(curr.matches("S_COMMIT")) {
                                    	process("S_COMMIT", sock, currRead, log, control, client);
                                    	sendDocument(sock,doc1);  
                                     }
                                    
                                        else if(curr.matches("LOGOFF")){
                                            logoff = true;
                                            control.deleteClientRecord(client);
                                            log.removeActiveConnection(client);
                                            sendResponse(RTCEMessageType.LACK, -1, -1, this.sock);
                                            flagConn = false;
                                            this.close();
                                        break;
                                    }
                                    
                                        else if(curr.matches("ABORT")){
                                            abort = true;
                                            control.deleteClientRecord(client);
                                            log.removeActiveConnection(client);
                                            flagConn = false;
                                            this.close();
                                        break;
                                    }
                                    
                                        else if(curr.matches("BLOCK")){
                                            if(owner)
                                            {
                                                process("BLOCK", sock, currRead, log, control, client);
                                            }
                                        }
                                     
                                        
                                
                               
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
  
  public boolean isTimeout() {
		return timeout;
	}
	
	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	public boolean isWaitTimeout() {
		return waitTimeout;
	}

	public void setWaitTimeout(boolean waitTimeout) {
		this.waitTimeout = waitTimeout;
	}

	public void startTimer(long time){
		timer = new Timer("Timeout Timer");
		waitTimeout = true;
		timeout = false;
		timer.schedule(new RTCETimeoutTask(), time);
	}
	
	public void cancelTimer(){
		timer.cancel();
		waitTimeout = false;
		timeout = false;
		timer = null;
	}
	
	public class RTCETimeoutTask extends TimerTask{
		@Override
		public void run(){
			if(waitTimeout){
				timeout = true;
			}else{
				timeout = false;
			}
			waitTimeout = false;
			timer.cancel();
			timer = null;
		}
	}
  
  public static void main(String arg[]) throws IOException
  {
      
      RTCEServerConfig.init("config/server/servConfig.conf");
      
      //Create and start the Discovery Thread
      RTCEDiscoveryServer discServer = new RTCEDiscoveryServer();
      Thread discThread = new Thread(discServer);      
      discThread.start();
      ServerSocket listenSock = new ServerSocket(25351);
     // ServerSocket listenSock2 = new ServerSocket(50000);
      RTCEServer server = new RTCEServer();
      while(true)
      {
           //create a new socket here which is free
          
          RTCEServer server1 = new RTCEServer(listenSock.accept());          
          Thread thread = new Thread(server1);          
          thread.start();
          
          
      }
      
      
  }       
  
  
}
