package rtce.client;

import rtce.RTCEMessageType;
import rtce.RTCEConstants;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;


public class RTCEClientMessage {

	//The request type
	private RTCEMessageType request, serverResponse;
	
	//The username, for appropriate messages
	private String username;
	
	//The password, for appropriate messages
	private String password;
	
	//The encryption options, for appropriate messages
	private String encryptOpts[];
	
	//The other option list, for appropriate messages
	private String genericOpts[];
	
	//The session ID for all headers
	private long sessionId;
	
        //The time stamp value
        private long timeStamp;
        
	//The checksum, for all headers
	private int checksum;
	
	//The reserved fields, for all headers
	private int headerReserved1;
	private int headerReserved2;
	private int headerReserved3;

	/**
	 * Get the message type
	 * @return The message type
	 */
	public RTCEMessageType getRequest() {
		return request;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setRequest(RTCEMessageType request) {
		this.request = request;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getEncryptOpts() {
		return encryptOpts;
	}

	public void setEncryptOpts(String[] encryptOpts) {
		this.encryptOpts = encryptOpts;
	}

	public String[] getGenericOpts() {
		return genericOpts;
	}
         
	public void setGenericOpts(String[] genericOpts) {
		this.genericOpts = genericOpts;
	}
	
	public void setRequest(byte[] requestChars){
		String requestName = new String(requestChars, RTCEConstants.getRtcecharset());
		request = RTCEMessageType.valueOf(requestName);
	}
	
	public byte[] getRequestChars(){
		byte[] requestName = new byte[RTCEConstants.getRequestLength()];
		byte[] requestBytes = request.toString().getBytes(RTCEConstants.getRtcecharset());
		if(requestBytes.length == RTCEConstants.getRequestLength()){
			requestName = requestBytes;
		}else{
                
			for(int i = 0; i < requestBytes.length; i++)
                        {
				requestName[i] = requestBytes[i];
			}
			for(int i = requestBytes.length; i < requestName.length; i++)
                        {
				requestName[i] = 0;
			}
		}
		return requestName;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
                
	}

	public int getChecksum() {
		return checksum;
	}

	public void setChecksum(int checksum) {
		this.checksum = checksum;
	}

        //We will tackle exact issue of timing when testing the control flow
        public void setTime()
        {
            this.timeStamp = System.nanoTime();
        }
        
        public long getTime()
        {
            return timeStamp;
        }
        
	public int getHeaderReserved1() {
		return headerReserved1;
	}

	public void setHeaderReserved1(int headerReserved1) {
		this.headerReserved1 = headerReserved1;
	}

	public int getHeaderReserved2() {
		return headerReserved2;
	}

	public void setHeaderReserved2(int headerReserved2) {
		this.headerReserved2 = headerReserved2;
	}

	public int getHeaderReserved3() {
		return headerReserved3;
	}

	public void setHeaderReserved3(int headerReserved3) {
		this.headerReserved3 = headerReserved3;
	}
        
    public ByteBuffer setHeader(RTCEMessageType request)
    {

        setRequest(request);
        ByteBuffer bbuf = ByteBuffer.allocate(40);
        bbuf.put(getRequestChars());
        bbuf.putLong(23);
        bbuf.putLong(getTime());
        bbuf.putInt(1001);
        bbuf.putInt(1);
        bbuf.putInt(2);
        bbuf.putInt(3);
        return bbuf;
        
    }       
   
     public void getHeader(ByteBuffer bf)
       {
          String request = new String(bf.array(), 0, 8);
          
          System.out.println("Request in string format: " + request);
          System.out.println(bf.position());
          
          bf.position(8);
          System.out.println("Session ID:   "+ bf.getLong());
          System.out.println("Time Stamp:   "+ bf.getLong());
          
          System.out.println("Checksum: "+ bf.getInt());
          System.out.println("Reserved1:  "+bf.getInt());
          System.out.println("Reserved 2: "+ bf.getInt());
          System.out.println("Reserved 3: "+ bf.getInt());
          
       }  
     
     
    // This function builds and transmits the Message on the supplied socket
    public void sendMessage(Socket s, RTCEMessageType option)
    {
        boolean validflag = true;
        
        //header information in messageheader
        ByteBuffer messageHeader = setHeader(option);
        
        //Initialization of Data and Control Message class objects
        
        DataMessage dataPayload = null;
        ControlMessage controlPayload = null;
        
       //Use the request type to build the message
       //CLIENT CAN SEND ONLY SPECIFIC MESSAGES, option with request for now
       switch (option) 
       {
      /* case CUAUTH:
          //payload = setCUAUTH(); 
    	  break;*/
      
        // S_TREQST for testing
       case S_TREQST:
         
            controlPayload = new ControlMessage(12);
            controlPayload.payload = controlPayload.setSTREQST();
     	  break;    
          
       case S_DONE:
       
           controlPayload = new ControlMessage(8);
            controlPayload.payload = controlPayload.setS_DONE();
           //payload = setS_DONE();
     	  break;    
          
        case S_COMMIT:
            controlPayload = new ControlMessage(21);
            controlPayload.payload = controlPayload.setS_COMMIT();
         	   
       case ABORT: {}
           //payload = setS_ABORT();
     	  break;    	   
       case ECHO: {}
           //payload = setECHO();
     	  break;    	   
      
       case LOGOFF: {}
           //payload = setLOGOFF();
     	  break;    	   
        	   
       case CACK:  {}
           //payload = setCACK();
     	  break;    	   
        
       default: validflag = false;
    	        System.out.println("2");  	
       } //switch (request)
       
       //Message is transmitted over here
       if(validflag)
       {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    out.write(messageHeader.array());
                    
                        if(controlPayload!=null)
                        out.write(controlPayload.payload.array());
                        
                    int a = out.size();
                    System.out.println(a);
                    s.getOutputStream().write(out.toByteArray(), 0, out.size());
                    
                    byte[] test = out.toByteArray();
                     String s1 = new String(test, 0, 8);
                     System.out.println("OUTGOING REQUEST" + s1 + "\n");
                    
                    }
                catch(IOException ex) 
                { System.err.println("IOException in sendMessage"); }       
       }   
    }   

    
    
    public int lengthBuffer(String s)
     {
         
            //Allocate buffer length according to request
          switch (s) 
          {
          //case "CUAUTH":
          //return 129;
       
          case "S_TREQST":
          return 52;  
          	   
          case "S_DONE":
           return 48;
           
          case "S_COMMIT":
           return 61;    
          
           case "ABORT":
           return 40;
          
           case "ECHO":
            return 40;   	
          
           case "LOGOFF":
           return 40; 
          
           case "CACK":
            return 40; 

         default: return 0;	        
          
        }
     }
     public int lastByte(byte[] asciiVal)
     {
        for(int i=0;i<asciiVal.length;i++)
            {
                int current = new Byte(asciiVal[i]).intValue();
                if(current == 0)
                {
                     return i;
                }
            }
        return 8;
     }   
     
// This function builds and transmits the Message on the supplied socket
    public void recvMessage(Socket s, RTCEMessageType request, ByteBuffer bf)
    {
        //Extract header contents first, needed to make a function out of this
        getHeader(bf);
        ControlMessage control = new ControlMessage();
        
       switch (request) 
       {
         /* case CONNECT:
          //payload = setCUAUTH(); 
    	  break;
           
           /* case S_LIST:
          //payload = setCUAUTH(); 
    	  break;
           
           /* case S_DATA:
          //payload = setCUAUTH(); 
    	  break;
          */
       
          case S_TRESPN:
          control.getS_TRESPN(bf);
     	  break;     
          
          case S_DENIED:
           control.getS_DENIED(bf);
            {}
     	  break;    
          
          case S_REVOKE:
           control.getS_REVOKE(bf);
            {}
     	  break;    
          
         case ABORT:
           System.out.println("ABORT \n");
             {}
     	  break; 
          
       case ECHO:
           System.out.println("ECHO \n");
            {}
     	  break;    	
          
       case LACK:
            System.out.println("LACK \n");
             {}
     	  break;  

       default: {}
    	        	
       } //switch (request)
            
    }  
}

//Seperate Class for Delineation needed else difficult to debug
class DataMessage extends RTCEClientMessage
{

    // This function clears the Section List stored
    // used only for S_LIST MessageType
    
    public void clearSectionList() 
    {
      //To be filled in by Anthony
    }
    
    // This function adds a section range to the message.
    // used only for S_LIST MessageType
    public void addToSectionList(int Start, int Finish) 
    {
      //To be filled in by Anthony	
    }
    
    // This function sets the data for the S_DATA MessageType
    public void setSectionData(int SectionID, int SeqID, int CompleteID, String Data)
    {
      //To be filled in by Anthony    	
    }
    

}

//Control Messages and everything Else, inheerits both classes | Testing for now
class ControlMessage extends DataMessage
{
   
   ByteBuffer payload;
   int messagetype;
   
   ControlMessage(){}
   
   ControlMessage(int byteSize)
   {
       payload = ByteBuffer.allocate(byteSize);
   }
   
   /*ALL SENDING CONTROL MESSAGES*/
   
     public ByteBuffer setSTREQST() 
    {
         //arbitray for now
        int sectionId = 100;
        int length = 12;
        byte[] options = new byte[4];
        
        //set U and V bit for now
        options[0] = (byte) (options[0] | (1 << 0) | (1 << 1));
        
        ByteBuffer b = ByteBuffer.allocate(12);
        b.putInt(sectionId);
        b.putInt(length);
        b.put(options);
        return b; 
    }
     
       public ByteBuffer setS_DONE()
       {
           //status code 1 means no error
           int statuscode = 1;
           int error = 0;
            ByteBuffer b = ByteBuffer.allocate(8);
            b.putInt(statuscode);
            b.putInt(error);
            return b;
       }

       public ByteBuffer setS_COMMIT()
       {
           byte[] token = new byte[16];
           
           for(int i=0; i<16; i++)
             token[i] = (byte)0xe0;
             
           int length = 21;
           byte[] data = new byte[1];
           
             for(int i=0; i<1; i++)
               data[i] = (byte)0xe0;
             
           ByteBuffer b = ByteBuffer.allocate(21);
           b.put(token);
           b.putInt(length);
           b.put(data);
           return b;
       }
       
       //ALL RECEIVED MESSAGES FROM SERVER SIDE
       public void getS_TRESPN(ByteBuffer bf)
    {   
           bf.position(40);
           System.out.println("token processing..");
           bf.position(56);
           System.out.println("additional priviledges..");
          
    }
       
      public void getS_DENIED(ByteBuffer bf)
    {   
           bf.position(40);
           System.out.println("Status Code:" + bf.getInt());
           System.out.println("Error Code:" + bf.getInt());
    }
    
      public void getS_REVOKE(ByteBuffer bf)
    {   
           bf.position(40);
           System.out.println("Status Code:" + bf.getInt());
           System.out.println("Error Code:" + bf.getInt());
    }  
    
       
       
}