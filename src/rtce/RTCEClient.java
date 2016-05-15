
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

        //Constants used for Discovery
    	final static int    DISCOVERY_PORT = 4446;
    	final static String MCAST_DISCOVERY_GROUP = "225.0.0.10";	
        
        RTCEClient(int port) throws IOException, UnknownHostException
                {
        	        
                    sock = new Socket (discoverServer(), port);
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
        
        
        /* discoverServer will attempt to locate the IP address of any RTCE server
         *     if none could be found the loopback adapter 127.0.0.1 will be returned
         */
        String discoverServer()
        {
        	byte[] outboundBuf = new byte[4];
        	
        	try{
            //Create the socket and join the Multicast Group for Discovery
        	MulticastSocket socket = new MulticastSocket(DISCOVERY_PORT);
            InetAddress m_group = InetAddress.getByName(MCAST_DISCOVERY_GROUP);
            socket.joinGroup(m_group);
        	
            //Create the Datagram with this client's IP Address as the payload
            DatagramPacket outboundPacket;
            System.out.println("Client " + InetAddress.getLocalHost().getHostAddress() + 
            		" is searching for a server...");
            outboundBuf = InetAddress.getLocalHost().getAddress();
            outboundPacket = new DatagramPacket(
            		outboundBuf,
            		outboundBuf.length,
            		m_group,DISCOVERY_PORT);
            
            //Prepare to send packet and receive response from server
            int attempts = 0;
            socket.setSoTimeout(1000);     //Give the server 1 second to respond            
            byte[] responseBuf = new byte[2];
            DatagramPacket responsePacket;
            responsePacket = new DatagramPacket(
            		responseBuf,
            		responseBuf.length,
            		m_group,DISCOVERY_PORT);
                        
            //Attempt discovery
            while (attempts < 10)
            {  socket.send(outboundPacket);
               try {            	 
                 socket.receive(responsePacket);
                 if (responseBuf[0] == 'H' & responseBuf[1] == 'I')
                 { System.out.println("Found Server: " + responsePacket.getAddress());
                   socket.close();
                   return responsePacket.getAddress().getHostAddress();
                 }
               } catch (java.net.SocketTimeoutException e)
               { attempts++;
                 System.out.print(".");
               }                   
            }
            
            //No server was found
            System.out.println("No servers found.   Defaulting to 127.0.0.1");
            socket.close();
            return "127.0.0.1";
        	} //try block
            catch (IOException e) {
            	e.printStackTrace(); }
        	
        	return "127.0.0.1";
        } // end discoverServer()
        
        
    public static void main(String[] args) throws IOException
    {
        final int servPort = 25351; //Server Port        
        int i =0;
        
        
        RTCEClient client = new RTCEClient(servPort);
        
        
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
