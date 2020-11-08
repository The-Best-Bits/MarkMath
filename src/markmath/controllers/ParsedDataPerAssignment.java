package markmath.controllers;

import java.util.HashMap;

public class ParsedDataPerAssignment {
    /** This class further parses the data received from Hypatia from one specific document, putting it in a
     * format for which it can be used throughout the program.
     *Each ParsedDataPerAssignment stores the name of the assignment to which it refers once created, a dictionary of
     * form {"question1": number of errors for question one, and so on for the other questions }.
     */
    private String assignmentName;
    private String studentName;
    private HashMap<String, Integer> finalParsedData;

    /**
     *Creates a ParsedDataPerAssignment object for a document; it stores all the information parsed by Hypatia from
     * the document, and this information is continuously updated. Stores assignment Name, the student Name that
     * owns the document, and a map for questions and their errors.
     * @param parsedData The data parsed by CheckmathParser from Hypatia specific to this one document.
     */
    public ParsedDataPerAssignment(HashMap<String, Object> parsedData){
        this.assignmentName = (String) parsedData.get("documentName");
        this.studentName = (String) parsedData.get("studentName");
        this.finalParsedData = new HashMap<>();
        String question = ("question" + (int)  parsedData.get("problemNumber"));
        this.finalParsedData.put(question, (Integer) parsedData.get("errors"));

    }

    /**
     * Returns the assignment Name of the document this ParsedDataPerAssignment object corresponds to.
     * @return assignmentName
     */
    public String getAssignmentName(){

        return this.assignmentName;
    }

    /**
     * Adds new information parsed from CheckmatParser: if a new problem is received, the corresponding key and errors
     * are given a spot in the HashMap finalParsedData; otherwise, the current value for errors corresponding to the
     * updated question is changed so that the new errors found have been added.
     * @param newParsedData The new Data parsed from the document.
     */
    public void addParsedData(HashMap<String, Object> newParsedData){
        String receivedQuestion = "question" + (int) newParsedData.get("problemNumber");
        if (finalParsedData.containsKey(receivedQuestion)) {
            int old_errors = finalParsedData.get(receivedQuestion);
            finalParsedData.replace(receivedQuestion, old_errors + (int) newParsedData.get("errors"));
        }
        else{
            finalParsedData.put(receivedQuestion,(int) newParsedData.get("errors"));
        }

    }

    /**
     * Returns the ParsedData for this document so that it can be used in the program.
     * @return the map finalParsedData that maps question to errors collected through the server.
     */
    public HashMap<String, Integer> getFinalParsedData(){
        return finalParsedData;
    }

}
