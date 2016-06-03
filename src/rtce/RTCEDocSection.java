package rtce;

/**
 * RTCEDocSection
 * This class represents a single section in the document
 * @author Anthony Emma, Edwin Dauber, Ankush Israney, Francis Obiagwu
 * @date Friday, June 3, 2016
 * @version 1
 */
public class RTCEDocSection {

	private int ID;     //The Section ID
	private String txt; //The text for this section 

	/**
	 * Create a new document section
	 */
	public RTCEDocSection()
	{ ID = 0;}

	/**
	 * Set a section to have specific id and text
	 * @param inID - the new section id
	 * @param inTxt - the new section text
	 */
	public void setSection(int inID, String inTxt)
	{ ID = inID;
	txt = inTxt; 
	}

	/**
	 * Get the section id
	 * @return the section id
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Set the section id
	 * @param iD - the new section id
	 */
	public void setID(int iD) {
		ID = iD;
	}

	/**
	 * Get the section text
	 * @return the section text
	 */
	public String getTxt() {
		return txt;
	}

	/**
	 * Set the section text
	 * @param txt - the new section text
	 */
	public void setTxt(String txt) {
		this.txt = txt;
	}

}
