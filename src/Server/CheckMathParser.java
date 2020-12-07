package Server;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import markmath.controllers.ParsedDataPerAssignment;
import markmath.controllers.ParsedDataPerAssignmentManager;

import java.util.*;


public class CheckMathParser {
    /** Following Clean Architecture this is an entity class.
     * Responsible for parsing and combining all of the "result" event data received by SocketIOServer from a single
     * Hypatia document.
     * Attributes:
     * docnameParser: the docname parsing algorithm used for this CheckMathParser (docname is the name of the Hypatia
     * document sent through all "result" events for this Hypatia document)
     * assignmentname: name of this document (equivalent to docname)
     * assignmentType: the type of assignment the document belongs to (contained in docname)
     * studentNum: the student number of the student who this Hypatia document belongs to (contained in docname)
     */

    private DocumentNameParsingAlgorithm docNameParser;
    private String assignmentName;
    private String assignmentType;
    private String studentNum;
    private HashMap<String, Integer> finalParsedData;


    /**
     * When a CheckMathParser object is instantiated, the docname parsing algorithm is set to ParseByDash by default.
     * This means that the student document being marked must be of the form StudentNumber-AssignmentType
     * (spaces are allowed)
     */
    public CheckMathParser(){
        docNameParser = new ParseByDash();
        finalParsedData = new HashMap<>();
    }


    /**
     * Sets the String attributes of this CheckMathParser object (Assignment Name, Assignment Type, Student Number)
     * using 'data' (which in the context of our program is the first received "result" event from an opened Hypatia
     * document). The question number and amount of errors for this question as given in 'data' is also recorded in
     * finalParsedData
     * @param data JSON String data from SocketIOServer
     * @throws JsonProcessingException Library Jackson is used to covert the JSON String to a HashMap. When there is an
     * error in processing the JSON String in ObjectMapper().readValue() method this exception is thrown.
     *
     */
    public void parseFirstResultEvent(String data) throws JsonProcessingException {
        //instantiate new HashMap from JSON String using jackson library
        Map<String, Object> tempResultsMap = new ObjectMapper().readValue(data, HashMap.class);

        try{
            ArrayList<String> docInfo = docNameParser.getDocNameInfo((String)tempResultsMap.get("docname"));
            this.studentNum = docInfo.get(0);
            this.assignmentType = docInfo.get(1);
            this.assignmentName = docInfo.get(2);
            String question = ("question" + (int)tempResultsMap.get("problem"));
            this.finalParsedData.put(question, checkForErrors((LinkedHashMap)tempResultsMap.get("value")));
            System.out.println( assignmentName + assignmentType + studentNum + finalParsedData);

        }catch(InvalidDocumentNameException e){
            System.out.println("Invalid Document Name");
        }

    }


    /**
     * Adds new "result" event data received from the SocketIOServer to finalParsedData. If the new 'result' data
     * corresponds to a question already in finalParsedData we update the amount of errors for this question. If the
     * opposite is true we add a new question with the corresponding amount of errors from the received 'result' data to
     * finalParsedData
     * @param data new 'result' event data received from SocketIOServer
     */
    public void addParsedData(String data) throws JsonProcessingException{
        //instantiate new HashMap from JSON String using jackson library
        Map<String, Object> tempResultsMap = new ObjectMapper().readValue(data, HashMap.class);
        String receivedQuestion = "question" + (int)tempResultsMap.get("problem");
        if (finalParsedData.containsKey(receivedQuestion)) {
            int old_errors = finalParsedData.get(receivedQuestion);
            finalParsedData.replace(receivedQuestion, old_errors +
                    checkForErrors((LinkedHashMap)tempResultsMap.get("value")));
        }
        else{
            finalParsedData.put(receivedQuestion,checkForErrors((LinkedHashMap)tempResultsMap.get("value")));
        }

    }

    /**
     * Checks whether a 'result' event contains an error
     * @param valueError LinkedHashMap containing a key "type" mapped to the type of error in the 'result' event
     * @return the number of errors given by this results event
     */
    private int checkForErrors(LinkedHashMap valueError) {
        if (valueError.get("type").equals("correct")){
            return 0;
        }
        else{
            return 1;
        }
    }

    /**
     * Clears finalParsedData. After a student document has been marked this method is called.
     */
    public void clearCheckMathParser(){
        this.finalParsedData.clear();
    }

    /**
     * @return assignmentName
     */
    public String getAssignmentName(){
        return this.assignmentName;
    }

    /**
     * @return assignmentType
     */
    public String getAssignmentType(){
        return this.assignmentType;
    }

    /**
     * @return studentNum
     */
    public String getStudentNum(){
        return this.studentNum;
    }

    /**
     * @return finalParsedData
     */
    public HashMap<String, Integer> getFinalParsedData(){
        return this.finalParsedData;
    }
}
