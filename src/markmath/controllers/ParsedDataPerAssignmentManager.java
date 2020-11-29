package markmath.controllers;

import javax.security.sasl.SaslServer;
import java.util.ArrayList;
import java.util.HashMap;

public class ParsedDataPerAssignmentManager {
    /**
     * Manages all of the parsedDataPerAssignment objects
     *
     * Attributes:
     * parsedDataAssignments: list of pre-existing parsedDataPerAssignment objects
     */

    private ArrayList<ParsedDataPerAssignment> parsedDataAssignments;

    public ParsedDataPerAssignmentManager(){
        parsedDataAssignments = new ArrayList<>();
    }

    /**
     * Receives parsedData from CheckMathParser and either creates a new parsedDataPerAssignment or passes
     * the parsedData to the corresponding pre-existing parsedDataPerAssignment
     * @param parsedData parsed data from CheckMathParser
     */
    public void manageParsedData(HashMap<String, Object> parsedData){
        ParsedDataPerAssignment preExistingAssignment = isParsedDataAssignment((String)parsedData.get("documentName"));
        if (preExistingAssignment == null ){
            System.out.println("Create new Assignment");
            ParsedDataPerAssignment newAssignment = new ParsedDataPerAssignment(parsedData);
            parsedDataAssignments.add(newAssignment);
        }
        else{
            System.out.println("Add Parsed Data");
            preExistingAssignment.addParsedData(parsedData);
        }
    }

    /**
     * Checks if there is a parsedDataPerAssignment object corresponding to this document name in the
     * parsedDataAssignments list
     * @param documentName name of the document associated with the received parsed data
     * @return the pre-existing parsedDataPerAssignment object with this document name or null if none exists
     */
    private ParsedDataPerAssignment isParsedDataAssignment(String documentName){
        for (ParsedDataPerAssignment item: parsedDataAssignments){
            if (item.getAssignmentName().equals(documentName)){
                return item;
            }
        }
        return null;
    }

    /**
     * Method is currently only being used for testing, but may be used in our program later in developement
     * @return ArrayList of ParsedDataAssignments
     */
    public ArrayList<ParsedDataPerAssignment> getParsedDataAssignments(){return this.parsedDataAssignments;}

    /**
     * Clear parsedDataAssignments list (once the mark button has been clicked)
     */
    public void clearParsedDataAssignments(){this.parsedDataAssignments.clear();}
}
