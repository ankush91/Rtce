
/**
 *
 * @author GROUP 4
 * @version 1
 * 
 */

package rtce.client;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import rtce.RTCEConstants;
import rtce.RTCEDocument;
import static rtce.RTCEConstants.getRtcecharset;
import rtce.RTCEMessageType;
import rtce.server.RTCEDiscoveryServer;
public class RTCEClient {

        static Socket sock;
        OutputStream sendStream;
        InputStream recvStream;
        String request, response;
        RTCEClientAuth cAuthModule;
        static RTCEDocument doc = new RTCEDocument(0);

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
            this.request = "echo";
        }
        
        void sendRequest(RTCEMessageType request)
        {
            RTCEClientMessage clientMessage = new RTCEClientMessage();
            clientMessage.sendMessage(sock, request);
            
           
        }
        
        public RTCEClientAuth getcAuthModule() {
			return cAuthModule;
		}

		public void setcAuthModule(RTCEClientAuth cAuthModule) {
			this.cAuthModule = cAuthModule;
		}
		
		void getResponse()
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
          
          RTCEClientMessage serverMessage = new RTCEClientMessage(); 
           String s = new String(asciiVal, 0, serverMessage.lastByte(asciiVal), RTCEConstants.getRtcecharset());
          serverMessage.setDocument(doc);
         
          if((messageSize = serverMessage.lengthBuffer(s))!=0)
          {   
            ByteBuffer bf = ByteBuffer.allocate(messageSize);
            System.out.println(bf.capacity());
            
            bf.put(readbf);
            response = "";
            for(int i = 0; i < s.length(); i++){
            	if(s.charAt(i) == 0){
            		break;
            	}
            	response += s.charAt(i);
            }
            System.out.println("INCOMING RESPONSE:  "+response+"\n");
            serverMessage.recvMessage(sock, RTCEMessageType.valueOf(response), bf);
          }
          
          else{}
          
      }
      
      catch(IOException ex)
      {
          System.err.println("IOException in getRequest");
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
    	RTCEClientConfig.init("config/client/clientConfig.conf");
        final int servPort = 25351; //Server Port       
         
        RTCEClient client = new RTCEClient(servPort);
        
        RTCEClientUIInput UI_Input = new RTCEClientUIInput(sock, client);
        Thread UI_InputThread = new Thread(UI_Input);      
        UI_InputThread.start();

        RTCEClientUIOutput UI_Output = new RTCEClientUIOutput();
        UI_Output.setDocument(doc);
        Thread UI_OutputThread = new Thread(UI_Output);      
                
        
        UI_OutputThread.start();
        //String s = null;
        //Scanner sc = new Scanner(System.in);
               
        //while(s !="quit")
        //{
                //while(!sc.hasNextLine()){}
                 //s =  sc.next();
                 //client.sendRequest(RTCEMessageType.valueOf(new String(s.getBytes(), getRtcecharset())));
                 //client.getResponse();   
                 //Here's what I'd like this loop to look like now that UI exists (will talk on Skype):
                 // client.getResponse();
                 // UI_Output.refreshUI();
                 
                 
        //}
        
        
        //client.close();
      
       
    }
    
    
}
