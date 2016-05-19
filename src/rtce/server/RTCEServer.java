
/**
 *
 * @author GROUP4
 * @version 1
 */

package rtce.server;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import rtce.RTCEMessageType;



public class RTCEServer implements Runnable
{
    Socket sock;
    InputStream recvStream;
    OutputStream sendStream;
    String request;
    String response;
    
    RTCEServer (Socket s) throws IOException
    {
        sock = s;
        recvStream = sock.getInputStream();
        sendStream = sock.getOutputStream();
    } 
    
    public void run()
    {
      boolean flagConn = true;
        while(flagConn)
        {
        getRequest();
        process();
        sendResponse();
        }
        
        close();
        
    }       
    
  void getRequest()
  {
      try
      {
          int dataSize;
          while((dataSize = recvStream.available())==0);
          byte readbf[] = new byte[dataSize];
          
          System.out.println(dataSize);
          
          ByteBuffer bf = ByteBuffer.allocate(52);
          recvStream.read(readbf, 0, dataSize);
          
          bf.put(readbf);
          request = new String(readbf, 0, 8);
           
          RTCEServerMessage clientMessage = new RTCEServerMessage(); 
          clientMessage.recvMessage(sock, RTCEMessageType.valueOf(request), bf);
          
      }
      
      catch(IOException ex)
      {
          System.err.println("IOException in getRequest");
      }
  }
  
  void process()
  {
      //ADD code to use the string and provide the response.
      response = request;
  }
  
  void sendResponse()
  {
      try
      {
          System.out.println("Response in String Format: " + response);
          byte[] sendBuff = new byte[response.length()];
          sendBuff = response.getBytes();
          sendStream.write(sendBuff, 0, sendBuff.length);
          
      }
      
      catch(IOException ex)
      {
          System.err.println("IOException in getRequest");
      }
      
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
