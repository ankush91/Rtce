
/**
 *
 * @author GROUP 4
 * @version 1
 * 
 */

package rtce;

import java.net.*;
import java.io.*;
public class RTCEClient {

        Socket sock;
        OutputStream sendStream;
        InputStream recvStream;
        String request, response;
        
        RTCEClient(String server, int port) throws IOException, UnknownHostException
                {
                    sock = new Socket (server, port);
                    sendStream = sock.getOutputStream();
                    recvStream = sock.getInputStream();
                }
        
        void makeRequest()
        {
            //Add code to make request string.
            request = "echo";
        }
        
        void sendRequest()
        {
            try
            {
                //byte[] sendBuff = new byte[request.length()];
                //sendBuff = request.getBytes();
                sendStream.write("echo".getBytes(), 0, "echo".getBytes().length);
            } 
            catch (IOException ex) 
            {
                System.err.println("IOException in sendRequest");
            }
                
        }
        
        void getResponse()
        {
            try
            {
             int dataSize=0;
             System.out.println("iteration");
             while ((dataSize = recvStream.available())==0);
             byte[] recvBuff = new byte[dataSize];
             recvStream.read(recvBuff, 0, dataSize);
             response = new String(recvBuff, 0, dataSize);
            }
            catch (IOException ex) 
            {
                System.err.println("IOException in sendRequest");
            }
        }
        
        void useResponse()
        {
            //Add code to use the response string here.
            System.out.println(response+"\n");
        }
        
        void close()
        {
            try
            {
                sendStream.close();
                recvStream.close();
                sock.close();
          
            }
            catch(IOException ex)
            {
                System.err.println("IOException in close");
            }
        }       
        
        
    public static void main(String[] args) throws IOException
    {
        final int servPort = 25351; //Server Port
        final String servName = "127.0.0.1"; //Server Name
        int i =0;
        
        RTCEClient client = new RTCEClient(servName, servPort);
        
        while(i<10)
        {
        
        client.makeRequest();
        client.sendRequest();
        client.getResponse();
        client.useResponse();
        
        System.out.print(i);
        i= i+1;
        
        }
        
        client.close();
      
       
    }
    
    
}
