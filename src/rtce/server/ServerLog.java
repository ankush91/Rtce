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
    private HashMap connection_list; 
    private InetAddress clientIp;
    private int clientPort;
    private long sessionId;
    
    ServerLog() //LOG OF ACTIVE CONNECTIONS ON SPECIFIC PORT NUMBERS
    {
        connection_list = new HashMap<Integer, ServerLog>(); //initialize Hashmap from port -> client session
    }
    
    ServerLog(long sessionId, InetAddress ip) //initalize an inidividual client object
    {
        this.sessionId = sessionId;
        clientIp = ip;
    }
    
    public void addActiveConnection(ServerLog clientObject, int port) //Map client to port
    {
        connection_list.put(port, clientObject);
    }
    
    public boolean checkConnection(int port) //check for connection on port number
    {
        ServerLog status = (ServerLog) connection_list.get(port);
        if(status == null)
        {
            return true;
        }
        
        return false;
        
    } 
    
    public long getSessionId()
    {
        return this.sessionId;
    }
    
    public ServerLog getClient(int port)
    {
        return (ServerLog) connection_list.get(port);
    }
    
    public void removeActiveConnection(int port) //remove from list of active connections
    {
        connection_list.remove(port);
    }
    
    
    
    
}