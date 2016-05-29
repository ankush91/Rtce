package rtce.client;

import java.io.IOException;
import rtce.RTCEDocument;

public class RTCEClientUIOutput implements Runnable
	{
	   private RTCEDocument document;
	   
	   //Objects to handle requests to refresh the GUI
	   private Object lock = new Object();
	   private boolean refreshUI = false;
	   
	   RTCEClientUIOutput() throws IOException
	   {}
	   
	   public void setDocument(RTCEDocument doc)
	   {  this.document = doc;   }
	
       public void run()
	   {
   	  
    	  redrawUI();
    	  
    	  while(true)
    	  {    		
    		  synchronized (lock) {
    			if (refreshUI)
    			{ redrawUI();
    			  refreshUI = false;}    			  
    		  }    		  
    		  try{Thread.sleep(500);} catch (Exception e){}
    	  }
	   } //run
       
	   private void redrawUI()
	   {
		   synchronized (lock) {
              System.out.println("--------------------------------------------");
              document.printDocument();
              System.out.println("");
              System.out.println("Enter Command>>");
		   }		   
	   }
       
	   //After creation this is the only routine expected to be called.
	   public void refreshUI()
	   {
		 synchronized (lock) {
		   refreshUI = true;
		 }	   
	   }
	   
	} //RTCEClientUIOutput