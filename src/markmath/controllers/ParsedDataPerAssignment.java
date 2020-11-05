package markmath.controllers;

import java.util.HashMap;

public class ParsedDataPerAssignment {

    private String assignmentName;
    //you decide how the parsed data will be formatted in this HashMap
    private HashMap<String, Object> finalParsedData;

    public ParsedDataPerAssignment(HashMap<String, Object> parsedData){
        //send parsedData to another method that parses it
    }

    public String getAssignmentName(){
        //temporary
        //this will return the value associated with the "assignmentName" key in the HashMap
        return this.assignmentName;
    }

    public void addParsedData(HashMap<String, Object> newParsedData){

    }
}
