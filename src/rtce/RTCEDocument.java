package rtce;

public class RTCEDocument {
	
    
	//An array of the sections in the document
	private RTCEDocSection[] sections = new RTCEDocSection[1000];
	
	//An array capturing the order that section IDs appear in the document from beginning to end
	private int[] sectionOrder = new int[1000];
	
	//The name of the document
	private String docName;
	
	//
	private int S_Itr = 0; 
	private int orderItr = 0;
	
    public RTCEDocument(int Example)
    {
      //Initialize all fields
      for(int i =0; i < 1000; i++)
      { sections[i] = new RTCEDocSection();
    	sections[i].ID = 0; 
        sectionOrder[i] = 0;
      }
      
      //If Example is 1, Populate the document with canned data (used by Server)
      if (Example == 1) 
      {
    	docName = new String("TheRaven");    	
    	addSection(0,1,"Once upon a midnight dreary, while I pondered, weak and weary, Over many a quaint and curious volume of forgotten lore");  
    	addSection(1,2,"While I nodded, nearly napping, suddenly there came a tapping, As of some one gently rapping, rapping at my chamber door");
        addSection(2,3,"’Tis some visitor,' I muttered, 'tapping at my chamber door—  Only this and nothing more.' ");
        addSection(3,4,"Ah, distinctly I remember it was in the bleak December; And each separate dying ember wrought its ghost upon the floor.");
        addSection(4,5," Eagerly I wished the morrow;—vainly I had sought to borrow From my books surcease of sorrow—sorrow for the lost Lenore");
        addSection(5,6,"For the rare and radiant maiden whom the angels name Lenore— Nameless here for evermore");        
      }
    }
    
    //This procedure adds a sectionID with txt after some other section ID in the document
    public void addSection(int afterSectionID, int sectionID, String txt)
    {
      int section_itr = 1; //itr to find an available sectionID object
      int order_itr   = 0; //itr to find the location in the document
      
      //Find the afterSectionID in the order array
      while (sectionOrder[order_itr] != afterSectionID && order_itr < 1000)
      { order_itr++; }
      
      //If it wasn't found, abort
      if (order_itr == 1000) {return; }
      
      //Find an unused section object 
      while (sections[section_itr].ID > 0 && section_itr < 1000)
      { section_itr++; }
      
      //If none are available, abort
      if (section_itr == 1000) {return; }
      
      //Populate the section object
      sections[section_itr].ID = sectionID;
      sections[section_itr].txt = txt;
      
      //Move all slots down to make room for this new section ID      
      int itr = 999;
      while (itr > order_itr + 1)
      { sectionOrder[itr] = sectionOrder[itr-1];
        itr--;                  
      }
      sectionOrder[order_itr+1] = sectionID;
      
      
    } //addSection()
    
    //This routine will add or update a section.   Unlike "addSection" this assumes
    //the section is already in the ordered list of sections
    public void updateSection(int sectionID, String txt)
    {
      for(int i = 0; i < 1000; i++)
      {
    	if (sections[i].ID == sectionID)
    	{
    		sections[i].txt = txt;
    		return;
    	}    	  
      }
      //Section does not exist, need to find an open spot to add
      for(int i = 1; i < 1000; i++)
      {
    	if (sections[i].ID == 0)
    	{ sections[i].ID = sectionID;
    	  sections[i].txt = txt;
    	  return;
    	}      
      }
            
      return;
    
    }
    public void modifySection(int SectionID, String txt)
    {
      for(int i = 0; i < 1000; i++)
      {
    	  if (sections[i].ID == SectionID)
    	  { sections[i].txt = txt;
    	    return;
    	  }
      }
    
    } //modifySection()
    
    //Finds a SectionID object given an ID
    public RTCEDocSection findSection(int ID) 
    {
      for(int i = 0; i < 1000; i++)
      {
    	  if (sections[i].ID == ID)  { return sections[i];}
       }
      return sections[0];
    } //findSection()
   
    //Prints out the entire document
    public void printDocument()
    {
      int itr = 1;
      
      System.out.println("Document: " + docName);
      System.out.println("--------------------------------------");
      
      while (sectionOrder[itr] > 0)
      {  System.out.print(sectionOrder[itr]+":");
         System.out.println(findSection(sectionOrder[itr]).txt);
         itr++;	  
      }
    }
    
    
    public int resetSectionItr()
    {
      S_Itr = 1;
      for(int i = 1; i < 1000; i++)
      {
        if (sectionOrder[i] == 0) {return i;}
      }
      return 0;
    } //resetSectionItr
    
    public RTCEDocSection getNextSectionItr()
    {
      int sID = sectionOrder[S_Itr++];      
      
      if (sID == 0){return sections[0];}
      return findSection(sID);
    } //getNextSectionItr()
    
    public RTCEDocSection getDocumentSection(int ID)
    {
      for(int i = 1; i < 1000; i++)
      {
    	if (sections[i].ID == ID)
    	{ return sections[i];}
      }
      return sections[0];    		
          
    } //getDocumentSection()
    
    public void clearOrder ()
    {
        for(int i =0; i < 1000; i++)
        { sectionOrder[i] = 0; }
        orderItr = 1;          
    }
    
    public void setOrder(int SectionID)
    {
      sectionOrder[orderItr++] = SectionID;      
    }
}
