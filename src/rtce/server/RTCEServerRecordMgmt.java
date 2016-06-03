

package rtce.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * 
 * RTCEServerRecordMgmt - This class is the core backend of the protocol. 
 * All The token management functions with binding of tokens to clients are performed by this class.
 * Token - This class just binds tokens to sections as Token objects which are assigned to client records in client record mgmt
 * @author Edwin Dauber, Ankush Israney, Anthony Emma, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 */
 
public class RTCEServerRecordMgmt
{
    
    static ArrayList freeToken_list; //list of free token values
    static HashMap sectionFree_list;
    static HashMap clientRecord_list; // list of client records - Mapping to section-token object
    static HashMap sectionToken_list; // list of section-token objects
    
    RTCEServerRecordMgmt()
    {
       freeToken_list = tokenInitialize(); //initialize a list of free tokens
       clientRecord_list = new HashMap<>(); //HashMap for client records
       sectionToken_list = new HashMap<>();   //HashMap for section-token objects
       sectionFree_list = sectionInitialize();
    }
    
     public static ArrayList tokenInitialize() //free token list - assigning random values to tokens -(Not truly random here for debugging)
    {
        ArrayList initial = new ArrayList<Integer>();
        
        for(int i=23; i<123; i++)
            initial.add((double)i);
        
        return initial;
        
    }
    
    public static HashMap sectionInitialize() //initialize values of sections in document
    {
        HashMap h= new HashMap<>();
        for(int i=0; i<100; i++)
           h.put(i, true);
        
        return h;
    }
    
    public boolean checkFreeSection(int section) //function to check if section is free
    {
        if(section > -1)
        {
           // System.out.println(section);
            //System.out.println(sectionFree_list.get(section));
            return (boolean) sectionFree_list.get(section);
            
        }    
        else
            return false;
    }
    
    public void insertClientRecord(RTCEServerLog clientId, Token token) //insert client - token mapping in clientRecord List
    {
        clientRecord_list.put(clientId, token); 
    }
    
    public double tokenGrant(RTCEServerLog clientId, int sectionId) //grant tokens to clients, replace mapping
    {
        Token newToken = allocateToken(sectionId); //allocate a token-section object; remove token from free token list
        sectionToken_list.put(newToken.sectionId, newToken.token); //insert the token in section token mapping
        sectionFree_list.replace(sectionId, true, false); //change section availability to false -> section availability independent of Token Grant
        clientRecord_list.replace(clientId, newToken); //give the token to the client
        //System.out.println(sectionFree_list.get(sectionId));
        return newToken.token;
    }
    
    public Token allocateToken(int sectionId) //allocate a token from free token list
    {
        double freeToken = (double) freeToken_list.get(0); //get the first free token
        freeToken_list.remove(0); //remove it from the list
        Token newToken = new Token(sectionId, freeToken); //create new section - token object
        return newToken; //return the object
  
    }
      
  public void tokenRevoke(RTCEServerLog clientId) //function to revoke a client token
    {
        int a;
        if(clientHasToken(clientId)){
        	Token oldToken = (Token)clientRecord_list.get(clientId); //get old token from client
        	freeToken_list.add(oldToken.token); //put the token Value in free token list
        	clientRecord_list.replace(clientId, null); //map a null token to client
        	sectionFree_list.replace(oldToken.sectionId, false, true); //Free the section corresponding to the token
        }
    }  
    
  
  public boolean clientHasToken(RTCEServerLog clientId) //function to check if client currently has token
  {
      Token token = (Token)clientRecord_list.get(clientId);
      if(token!=null && token.token > -1) //client has token then return true
          return true;
      else
          return false;  //else return false
          
  }
    
    public void deleteClientRecord(RTCEServerLog clientId) //delete an individual client record
    {
        boolean sectionTest = checkClientTokenTest(clientId);
        if(sectionTest)
        {
            Token token = (Token)clientRecord_list.get(clientId);
            freeSection(token.sectionId);
            clientRecord_list.replace(clientId, null);
            
        }
            
        clientRecord_list.remove(clientId); //delete the client record for specific session
        
    }
    
    public double checkClientToken(RTCEServerLog clientId) //check if client has a token and return the token
    {
        Token tok = (Token)clientRecord_list.get(clientId);
        
      if(tok!=null)
        {    
            if(tok.token > -1)
                return tok.token;
            else
                return -1;
        }
      else
          return -1;
    }
    
    public boolean checkClientTokenTest(RTCEServerLog clientId) //only check if client has the token
    {
        Token tok = (Token)clientRecord_list.get(clientId);
       
      if(tok!=null)
        {    
            if(tok.token > (double)-1)
                return true;
            else
                return false;
        }
      else
          return false;
    }
    
    public void freeSection(int section) //to put an individual section in free section list
    {
        sectionFree_list.put(section, true);
    }
         
    
}

class Token //TOKEN CLASS CONTAINS SECTION-TOKEN MAPPING
{
    double token;
    int sectionId;
   
    Token(){}
    
    Token(int sectionId, double token) //initialize token object
    {
        this.sectionId = sectionId;
        this.token = token;
    }
    
    public Token deleteToken(Token token) // function to delete token
    {
        token = null;
        return token;
    }
}
