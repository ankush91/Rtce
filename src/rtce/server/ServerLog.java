/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtce.server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ankus
 */
public class ServerLog
{
    private static HashMap connection_list; 
    private InetAddress clientIp;
    private int clientPort;
    private long sessionId;
    private String userName;
    boolean block;
    
    ServerLog() //LOG OF ACTIVE CONNECTIONS
    {
        connection_list = new HashMap<Integer, ServerLog>(); //initialize Hashmap from port -> client session
    }
    
    ServerLog(long sessionId, InetAddress ip, String user) //initalize an inidividual client in Server Log
    {
        this.sessionId = sessionId;
        clientIp = ip;
        userName = user;
        block = false;
    }
    
    public boolean setBlock(String username) //FUNCTION TO BLOCK A USER
    {
        ServerLog l;
        boolean debug=false;
          for (Object key : connection_list.keySet()) {
            l = (ServerLog)key;
                if(l.userName.matches(username)){
                    l.block = true;
                    debug = true;
                    return true;
                }
            }
        if(debug==false)
            System.out.println("not found");
      return false;
      
    }     
    
    public boolean getBlock(ServerLog client) //return the block flag of a user
    {
        return client.block;
    }
    
    public void addActiveConnection(ServerLog clientObject, int port) //ADD CONNECTION - PORT MAPPING IN LOG
    {
        connection_list.put(clientObject, port);
    }
    
    public boolean checkConnection(ServerLog clientObject) //check for connection on client in log
    {
        ServerLog status = (ServerLog) connection_list.get(clientObject); 
        if(status == null)
        {
            return true;
        }
        
        return false;
        
    } 
    
    public long getSessionId() //RETURN SESSION ID OF ACTIVE CONNECTION
    {
        return this.sessionId;
    }
    
    
    public void removeActiveConnection(ServerLog clientObject) //REMOVE FROM LIST OF ACTIVE CONNECTIONS
    {
        connection_list.remove(clientObject);
    }
    
    public boolean checkOwner() //CHECK OWNER FOR DOCUMENT
    {
        if(connection_list.isEmpty())
            return true;
        else
            return false;
    }
    
    
    
    
}