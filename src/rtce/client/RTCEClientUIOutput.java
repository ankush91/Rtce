package rtce.client;

import java.io.IOException;
import rtce.RTCEDocument;

public class RTCEClientUIOutput implements Runnable
	{
	   private RTCEDocument document;
	   private Object lock = new Object();
	   private boolean startUI = false; 
	   private boolean refreshUI = false;
	   
	   RTCEClientUIOutput() throws IOException
	   {}
	   
	   public void setDocument(RTCEDocument doc)
	   {  this.document = doc;   }
	
       public void run()
	   {
    	  while(true)
    	  { synchronized (lock) {if (startUI) {break;} }
    	     try{Thread.sleep(500);} catch (Exception e){}
    	  }
    	  
    	  redrawUI();
    	  
    	  while(true)
    	  {    		
    		  synchronized (lock) {
    			if (refreshUI)
    			{
    			  redrawUI();
    			  refreshUI = false;
    			}
    			  
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
              System.out.println(">");
		   }		   
	   }
       
	   public void startUI()
	   {
		 synchronized (lock) {
		   startUI = true;
		 }	   
	   }
	   
	   public void refreshUI()
	   {
		 synchronized (lock) {
		   refreshUI = true;
		 }	   
	   }
	   
	} //RTCEClientUIOutput