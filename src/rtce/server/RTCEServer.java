
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
import rtce.RTCEDocument;

import rtce.RTCEMessageType;



public class RTCEServer implements Runnable
{
    Socket sock;
    InputStream recvStream;
    OutputStream sendStream;
    String request;
    String response;
    
    RTCEDocument doc1 = new RTCEDocument(1);
    
    RTCEServer (Socket s) throws IOException
    {
        sock = s;
        recvStream = sock.getInputStream();
        sendStream = sock.getOutputStream();
    } 
    
    public void run()
    { 
      
      boolean flagConn = true;


      /*Anthony Test: This is temporary code created to verify that S_LIST is sent properly.
       *    Anthony needs this here to uncomment when testing S_LIST, S_DATA, until a UI is developed    
      RTCEServerMessage slistMessage = new RTCEServerMessage();
      slistMessage.setDocument(doc1);
      slistMessage.setRequest(RTCEMessageType.S_LIST);      
      slistMessage.sendMessage(sock,RTCEMessageType.S_LIST);
      RTCEServerMessage sdataMessage = new RTCEServerMessage();
      for (int i = 1; i <= 6 ;i++) 
      {
      sdataMessage.setDocument(doc1);
      sdataMessage.setRequest(RTCEMessageType.S_DATA);
      sdataMessage.setSectionID(i);
      sdataMessage.sendMessage(sock,RTCEMessageType.S_DATA);      
      try{Thread.sleep(1000);} catch (Exception e){System.out.println("exception!"+e);}          	  
      }
      */

      int i=0;
      String a[] = {"S_TRESPN", "S_DENIED", "ABORT", "ECHO", "BLOCK", "LACK", "S_REVOKE", "CONNECT"};
      while(flagConn)
        {
        getRequest();
        //process();

        sendResponse(RTCEMessageType.valueOf(new String(a[i].getBytes(), getRtcecharset())));
        i = (i+1)%a.length;
        }
        
        close();
        
    }       
    
  void getRequest()
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
          
          RTCEServerMessage clientMessage = new RTCEServerMessage(); 
           String s = new String(asciiVal, 0, clientMessage.lastByte(asciiVal));
         
         
          if((messageSize = clientMessage.lengthBuffer(s))!=0)
          {   
            ByteBuffer bf = ByteBuffer.allocate(messageSize);
            
            bf.put(readbf);
            request = new String(s.getBytes(),getRtcecharset());
            
            System.out.println("INCOMING REQUEST: " + request+ "\n");
            clientMessage.recvMessage(sock, RTCEMessageType.valueOf(request), bf);
          }
          
          else{
              System.out.println("NOT PROCESSED.. \n");
          }
          
      }
      
      catch(IOException ex)
      {
          System.err.println("IOException in getRequest");
      }
  }
  
  void process()
  {
      //ADD code to use the string and provide the response.
  
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
  
  public static void main(String arg[]) throws IOException
  {
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
