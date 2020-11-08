package markmath.controllers;

import java.util.HashMap;

public class ParsedDataPerAssignment {
    /** This class further parses the data received from Hypatia from one specific document, putting it in a
     * format for which it can be used throughout the program.
     * Attributes:
     * assignmentName: name of the assignment to which it refers once created
     * assignmentType: the type of this assignment
     * studentName: the student's name
     * finalParsedData: a dictionary of form {"question1": number of errors for question one, and so on for the other
     * questions }.
     */
    private String assignmentName;
    private String assignmentType;
    private String studentName;
    private HashMap<String, Integer> finalParsedData;

    /**
     *Creates a ParsedDataPerAssignment object for a document; it stores all the information parsed by Hypatia from
     * the document, and this information is continuously updated. Stores assignment Name, assignment type, the student
     * Name that owns the document, and a map for questions and their errors.
     * @param parsedData The data parsed by CheckmathParser from Hypatia specific to this one document.
     */
    public ParsedDataPerAssignment(HashMap<String, Object> parsedData){
        this.assignmentName = (String) parsedData.get("documentName");
        this.studentName = (String) parsedData.get("studentName");
        this.assignmentType = (String) parsedData.get("assignmentType");
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
     * Returns the assignment type of the document this ParsedDataPerAssignment object corresponds to
     * @return assignmentType
     */
    public String getAssignmentType(){
        return this.assignmentType;
    }

    /**
     * Returns the name of the student of the document this ParsedDataPerAssignment object corresponds to
     * @return studentName
     */
    public String getStudentName(){
        return this.studentName;
    }

    /**
     * Adds new information parsed from CheckmathcParser: if a new problem is received, the corresponding key and errors
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
