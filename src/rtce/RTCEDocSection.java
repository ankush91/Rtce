package rtce;

//This class represents a single section in the document
public class RTCEDocSection {

	public int ID;     //The Section ID
    public String txt; //The text for this section 

    public RTCEDocSection()
    { ID = 0;}
    
    public void setSection(int inID, String inTxt)
    { ID = inID;
      txt = inTxt; 
    }
    
}
