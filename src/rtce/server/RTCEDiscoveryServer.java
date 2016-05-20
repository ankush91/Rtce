package rtce.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/* The Discovery Server Thread is a seperate thread which is created only once by the RTCE Server
 * to facilitate IP Address discovery of the server by clients.   This thread will listen for 
 * Multicast messaging over the established DISCOVERY_PORT in the multicast group and respond back
 * with the "secret handshake" of "HI" to clients 
 */
public class RTCEDiscoveryServer implements Runnable
	{

	//Constants used for Discovery
	final static int    DISCOVERY_PORT = 4446;
	final static String MCAST_DISCOVERY_GROUP = "225.0.0.10";	
	
    RTCEDiscoveryServer() throws IOException
	   {	   }

    public void run()
	   {
		   		  
		  try{
           //Create the socket and join the Multicast Group for Discovery
 	       MulticastSocket socket = new MulticastSocket(DISCOVERY_PORT);
	       InetAddress m_group = InetAddress.getByName(MCAST_DISCOVERY_GROUP);
	       socket.joinGroup(m_group);
	       
           //Create the Response Datagram - "HI"
           DatagramPacket responsePacket;
           byte[] responseBuf = new byte[2];
           responseBuf[0] = 'H';
           responseBuf[1] = 'I';
           responsePacket = new DatagramPacket(
           		responseBuf,
           		responseBuf.length,
           		m_group,DISCOVERY_PORT);
           
           //socket.setLoopbackMode(true);  //Don't want to hear our own outbound msgs
                     
           //Configure the incoming message
           byte[] incomingBuf = new byte[4];
           DatagramPacket incomingPacket;
           incomingPacket = new DatagramPacket(
           		incomingBuf,
           		incomingBuf.length,
           		m_group,DISCOVERY_PORT);
           
           System.out.println("Discovery Thread Starting");           
           while (true)
           {  socket.receive(incomingPacket);
              if (incomingPacket.getLength() == 4)
              {
//This output was being too chatty            	  
//                System.out.println("Client " + 
//                                    incomingPacket.getAddress().getHostAddress() +
//                                    " is attempting to discover");
                socket.send(responsePacket);
              }
           }
           
       	} //try block
           catch (IOException e) {
           	e.printStackTrace(); }

		  
	   } //run()
    
	} //class RTCEDiscoveryServer

