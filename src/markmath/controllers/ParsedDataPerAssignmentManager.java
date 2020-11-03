package markmath.controllers;

import java.util.ArrayList;
import java.util.HashMap;

public class ParsedDataPerAssignmentManager {

    private ArrayList<ParsedDataPerAssignment> parsedDataAssignments;

    public ParsedDataPerAssignmentManager(){
        parsedDataAssignments = new ArrayList<>();
    }

    public void manageParsedData(HashMap<String, Object> parsedData){
        ParsedDataPerAssignment preExistingAssignment = isParsedDataAssignment((String)parsedData.get("documentName"));
        if (preExistingAssignment == null ){
            ParsedDataPerAssignment newAssignment = new ParsedDataPerAssignment(parsedData);
            parsedDataAssignments.add(newAssignment);
        }
        else{
            preExistingAssignment.addParsedData(parsedData);
        }
    }

    private ParsedDataPerAssignment isParsedDataAssignment(String documentName){
        for (ParsedDataPerAssignment item: parsedDataAssignments){
            if (item.getAssignmentName().equals(documentName)){
                return item;
            }
        }
        return null;
    }
}
