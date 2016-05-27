
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
    
    ArrayList freeToken_list; //list of free token values
    HashMap sectionFree_list;
    HashMap clientRecord_list; // list of client records - Mapping to section-token object
    HashMap sectionToken_list; // list of section-token objects
    
    ServerRecordMgmt()
    {
       freeToken_list = tokenInitialize(); //initialize a list of free tokens
       clientRecord_list = new HashMap<>(); //HashMap for client records
       sectionToken_list = new HashMap<>();   //HashMap for section-token objects
       sectionFree_list = new HashMap<>();
    }
    
    public static ArrayList tokenInitialize() //free token list - assigning random values to tokens
    {
        ArrayList initial = new ArrayList<Integer>();
        Random r = new Random();
        
        for(int i=0; i<100; i++)
            initial.add(r.nextInt());
        
        return initial;
        
    }
    
    public void insertClientRecord(clientRecord clientId, Token token) //insert records
    {
        clientRecord_list.put(clientId, token); 
    }
    
    public void tokenGrant(clientRecord clientId, int sectionId) //grant tokens to clients, replace mapping
    {
        Token newToken = allocateToken(sectionId); //allocate a token-section object; remove token from free token list
        sectionToken_list.put(newToken.sectionId, newToken.token); //insert the token in section token mapping
        
        sectionFree_list.put(sectionId, false); //change section availability to false -> section availability independent of Token Grant
        
        clientRecord_list.replace(clientId, this, newToken); //give the token to the client
    }
    
    public Token allocateToken(int sectionId) //allocate a token from free token list
    {
        int freeToken = (int) freeToken_list.get(0); //get the first free token
        freeToken_list.remove(0); //remove it from the list
        Token newToken = new Token(sectionId, freeToken); //create new section - token object
        return newToken; //return the object
  
    }
      
    public void tokenRevoke(clientRecord clientId)
    {
        int a;
        Token oldToken = (Token)clientRecord_list.get(clientId);
        freeToken_list.add(oldToken.token);
        clientRecord_list.replace(clientId, null);
    }  
    
    public void deleteClientRecord(clientRecord clientId)
    {
        clientRecord_list.remove(clientId); //delete the client record for specific session
    }
         
    
}

 class clientRecord //CLIENT RECORD CLASS
{
    ServerLog client;
    int priority;
    
    clientRecord(ServerLog clientId, int priority)
    {
        client = clientId;
        this.priority = priority;
    }
       
    
}

class Token //TOKEN CLASS CONTAINS SECTION-TOKEN MAPPING
{
    int token;
    int sectionId;
   
    Token(){}
    
    Token(int sectionId, int token)
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
