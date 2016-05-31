

package rtce.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author GROUP 4 - THIS CLASS MANAGES RECORDS FOR CLIENTS WITH RESPECT TO TOKENS AND SECTIONS CORRESPONDING TO THOSE TOKENS
 */
public class ServerRecordMgmt 
{
    
    static ArrayList freeToken_list; //list of free token values
    static HashMap sectionFree_list;
    static HashMap clientRecord_list; // list of client records - Mapping to section-token object
    static HashMap sectionToken_list; // list of section-token objects
    
    ServerRecordMgmt()
    {
       freeToken_list = tokenInitialize(); //initialize a list of free tokens
       clientRecord_list = new HashMap<>(); //HashMap for client records
       sectionToken_list = new HashMap<>();   //HashMap for section-token objects
       sectionFree_list = sectionInitialize();
    }
    
     public static ArrayList tokenInitialize() //free token list - assigning random values to tokens
    {
        ArrayList initial = new ArrayList<Integer>();
        
        for(int i=23; i<123; i++)
            initial.add((double)i);
        
        return initial;
        
    }
    
    public static HashMap sectionInitialize()
    {
        HashMap h= new HashMap<>();
        for(int i=0; i<100; i++)
           h.put(i, true);
        
        return h;
    }
    
    public boolean checkFreeSection(int section)
    {
        if(section > -1)
        {
            System.out.println(sectionFree_list.get(section));
            return (boolean) sectionFree_list.get(section);
            
        }    
        else
            return false;
    }
    
    public void insertClientRecord(ServerLog clientId, Token token) //insert records
    {
        clientRecord_list.put(clientId, token); 
    }
    
    public double tokenGrant(ServerLog clientId, int sectionId) //grant tokens to clients, replace mapping
    {
        Token newToken = allocateToken(sectionId); //allocate a token-section object; remove token from free token list
        sectionToken_list.put(newToken.sectionId, newToken.token); //insert the token in section token mapping
        sectionFree_list.put(sectionId, false); //change section availability to false -> section availability independent of Token Grant
        clientRecord_list.put(clientId, newToken); //give the token to the client
      
        return newToken.token;
    }
    
    public Token allocateToken(int sectionId) //allocate a token from free token list
    {
        double freeToken = (double) freeToken_list.get(0); //get the first free token
        freeToken_list.remove(0); //remove it from the list
        Token newToken = new Token(sectionId, freeToken); //create new section - token object
        return newToken; //return the object
  
    }
      
    public void tokenRevoke(ServerLog clientId)
    {
        int a;
        Token oldToken = (Token)clientRecord_list.get(clientId);
        freeToken_list.add(oldToken.token);
        clientRecord_list.put(clientId, null);
    }  
    
    public void deleteClientRecord(ServerLog clientId)
    {
        boolean sectionTest = checkClientTokenTest(clientId);
        if(sectionTest)
        {
            Token token = (Token)clientRecord_list.get(clientId);
            freeSection(token.sectionId);
            clientRecord_list.put(clientId, null);
            
        }
            
        clientRecord_list.remove(clientId); //delete the client record for specific session
        
    }
    
    public double checkClientToken(ServerLog clientId)
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
    
    public boolean checkClientTokenTest(ServerLog clientId)
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
    
    public void freeSection(int section)
    {
        sectionFree_list.put(section, true);
    }
         
    
}

class Token //TOKEN CLASS CONTAINS SECTION-TOKEN MAPPING
{
    double token;
    int sectionId;
   
    Token(){}
    
    Token(int sectionId, double token)
    {
        this.sectionId = sectionId;
        this.token = token;
    }
    
    public Token deleteToken(Token token)
    {
        token = null;
        return token;
    }
}
