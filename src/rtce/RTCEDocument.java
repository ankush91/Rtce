package rtce;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * RTCEDocument
 * An enumeration of valid message types
 * @author Anthony Emma, Edwin Dauber, Ankush Israney, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 */
public class RTCEDocument {


	//An array of the sections in the document
	private RTCEDocSection[] sections = new RTCEDocSection[1000];

	//An array capturing the order that section IDs appear in the document from beginning to end
	private int[] sectionOrder = new int[1000];

	//The name of the document
	private String docName;
	private String docOwn;
	private String docExt;

	//The path on the disc to the document
	private String docPath;

	//Iterators used to service functions
	private int S_Itr = 0; 
	private int orderItr = 0;

	/**
	 * Constructor for the hard-coded example document
	 * @param Example - 1 to create the example document
	 */
	public RTCEDocument(int Example)
	{
		docPath = null;
		docOwn = null;
		docExt = null;
		//Initialize all fields
		for(int i =0; i < 1000; i++)
		{ sections[i] = new RTCEDocSection();
		sections[i].setID(0);
		sections[i].setTxt("");
		sectionOrder[i] = 0;
		}

		//If Example is 1, Populate the document with canned data (used by Server)
		if (Example == 1) 
		{
			docName = new String("TheRaven");  
			addSection(0,1,"Once upon a midnight dreary, while I pondered, weak and weary, Over many a quaint and curious volume of forgotten lore");  
			addSection(1,2,"While I nodded, nearly napping, suddenly there came a tapping, As of some one gently rapping, rapping at my chamber door");
			addSection(2,3,"'Tis some visitor,' I muttered, 'tapping at my chamber door-  Only this and nothing more.' ");
			addSection(3,4,"Ah, distinctly I remember it was in the bleak December; And each separate dying ember wrought its ghost upon the floor.");
			addSection(4,5," Eagerly I wished the morrow;-vainly I had sought to borrow From my books surcease of sorrow-sorrow for the lost Lenore");
			addSection(5,6,"For the rare and radiant maiden whom the angels name Lenore- Nameless here for evermore");        
		}
	}

	/**
	 * Constructor for a file by the filepath
	 * This is not used in our implementation, but would be used as part of a full application
	 * @param filepath - the path to the file on the disc
	 * @throws IOException - an exception if it cannot read the file
	 */
	public RTCEDocument(String basepath, String owner, String title, String ext) throws IOException{
		for(int i =0; i < 1000; i++)
		{ sections[i] = new RTCEDocSection();
		sections[i].setID(0);
		sections[i].setTxt("");
		sectionOrder[i] = 0;
		}
		docPath = basepath + "/" + owner + "/" + title + ext;
		docOwn = owner;
		docName = title;
		docExt = ext;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(docPath), RTCEConstants.getRtcecharset()));
		String line = reader.readLine();
		int i = 0;
		while(line != null){
			addSection(i, i+1, line);
			line = reader.readLine();
			i++;
		}
		reader.close();
	}

	/**
	 * Set the document name
	 * @param theName - the name to give the document
	 */
	public void setDocumentName(String theName){
		docName = theName;	

	}

	/**
	 * This procedure adds a sectionID with txt after some other section ID in the document
	 * @param afterSectionID - the previous section id
	 * @param sectionID - the current section id
	 * @param txt - the text to place in the section
	 */
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
		while (sections[section_itr].getID() > 0 && section_itr < 1000)
		{ section_itr++; }

		//If none are available, abort
		if (section_itr == 1000) {return; }

		//Populate the section object
		sections[section_itr].setID(sectionID);
		sections[section_itr].setTxt(txt);

		//Move all slots down to make room for this new section ID      
		int itr = 999;
		while (itr > order_itr + 1)
		{ sectionOrder[itr] = sectionOrder[itr-1];
		itr--;                  
		}
		sectionOrder[order_itr+1] = sectionID;


	} //addSection()

	/**
	 * This routine will add or update a section.   Unlike "addSection" this assumes
	 * the section is already in the ordered list of sections.   This routine should
	 * only be used due to receipt of S_DATA
	 * @param sectionID - the section to update
	 * @param txt - the new text
	 */
	public void updateSection(int sectionID, String txt)
	{
		//Try and find the section
		for(int i = 0; i < 1000; i++)
		{
			if (sections[i].getID() == sectionID)
			{	sections[i].setTxt(txt);
			return;  	}    	  
		}

		//Section does not exist, need to find an open spot to add
		for(int i = 1; i < 1000; i++)
		{
			if (sections[i].getID() == 0)
			{ sections[i].setID(sectionID);
			sections[i].setTxt(txt);
			return;
			}      
		}

		return;    
	}


	/**
	 * This procedure modifies a section if it exists.
	 * @param SectionID - the section to modify
	 * @param txt - the text to place in the section
	 */
	public void modifySection(int SectionID, String txt)
	{
		for(int i = 0; i < 1000; i++)
		{
			if (sections[i].getID() == SectionID)
			{ sections[i].setTxt(txt);
			return;
			}
		}

	} //modifySection()

	/**
	 * Finds a SectionID object given an ID
	 * @param ID - the section id
	 * @return - the document section
	 */
	public RTCEDocSection findSection(int ID) 
	{
		for(int i = 0; i < 1000; i++)
		{
			if (sections[i].getID() == ID)  { return sections[i];}
		}
		return sections[0];
	} //findSection()

	/**
	 * Prints out the entire document
	 */
	public void printDocument()
	{
		int itr = 1;

		System.out.println("Document: " + docName);
		System.out.println("--------------------------------------");

		while (sectionOrder[itr] > 0)
		{  System.out.print(sectionOrder[itr]+":");
		System.out.println(findSection(sectionOrder[itr]).getTxt());
		itr++;	  
		}
	}

	/**
	 * Reset iteration over sections
	 * @return 0, as the first section to iterte through
	 */
	public int resetSectionItr()
	{
		S_Itr = 1;
		for(int i = 1; i < 1000; i++)
		{
			if (sectionOrder[i] == 0) {return i;}
		}
		return 0;
	} //resetSectionItr

	/**
	 * Get the current section and increment the iterator
	 * @return - the current section
	 */
	public RTCEDocSection getNextSectionItr()
	{
		int sID = sectionOrder[S_Itr++];      

		if (sID == 0){return sections[0];}
		return findSection(sID);
	} //getNextSectionItr()

	/**
	 * duplicate of the findSection method
	 * @param ID - the section id
	 * @return the document section
	 */
	public RTCEDocSection getDocumentSection(int ID)
	{
		for(int i = 1; i < 1000; i++)
		{
			if (sections[i].getID() == ID)
			{ return sections[i];}
		}
		return sections[0];    		

	} //getDocumentSection()


	/**
	 * Unorder the sections
	 */
	public void clearOrder ()
	{
		for(int i =0; i < 1000; i++)
		{ sectionOrder[i] = 0; }
		orderItr = 1;          
	}

	/**
	 * Put the given section id in the next section spot
	 * @param SectionID - the id of the next section
	 */
	public void setOrder(int SectionID)
	{
		sectionOrder[orderItr++] = SectionID;      
	}

	/**
	 * Get the document path
	 * @return the document path as a String
	 */
	public String getDocPath() {
		return docPath;
	}

	/**
	 * Edit the document after receiving a commit
	 * @param prevID - the previous section id
	 * @param sID - the modified section id
	 * @param txt - the text to put in the section
	 */
	public void processCommit(int prevID, int sID, String txt)
	{
		//First see if the section exists already
		if (findSection(sID).getID() == sID)	
		{ modifySection(sID,txt); }
		else
		{ addSection(prevID,sID,txt); }
	}

	/**
	 * Get the sections
	 * @return the sections of the document
	 */
	public RTCEDocSection[] getSections() {
		return sections;
	}

	/**
	 * Get the section order
	 * @return the section order
	 */
	public int[] getSectionOrder() {
		return sectionOrder;
	}

	/**
	 * Get the document name
	 * @return the document name
	 */
	public String getDocName() {
		return docName;
	}

	/**
	 * Get the document owner
	 * @return the document owner
	 */
	public String getDocOwn() {
		return docOwn;
	}

	/**
	 * Get the section iterator
	 * @return the section iterator
	 */
	public int getS_Itr() {
		return S_Itr;
	}

	/**
	 * Get the order iterator
	 * @return the order iterator
	 */
	public int getOrderItr() {
		return orderItr;
	}

	/**
	 * Get the document extension
	 * @return the document extension
	 */
	public String getDocExt() {
		return docExt;
	}

	/**
	 * Override equals method for documents using either path or owner and name
	 * @param - an object to test
	 * @return - true if equal, false otherwise
	 */
	public boolean equals(Object o){
		if(o instanceof RTCEDocument){
			RTCEDocument d = (RTCEDocument) o;
			if(d.getDocPath().equals(docPath) || (d.getDocOwn().equals(docOwn) && d.getDocName().equals(docName))){
				return true;
			}
		}
		return false;
	}
}
