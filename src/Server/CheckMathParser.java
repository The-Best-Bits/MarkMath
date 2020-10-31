package Server;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;


public class CheckMathParser {

    /*The CheckMathParser Controller Class
     * Responsible for parsing the data received by SocketIOServer. For each results event received by SocketIOServer,
     * CheckMathParser will parse the event for the student name, assignment type, document name, problem number, and
     * number of errors
     */

    /**
     * Takes in the JSON String data from the results event caught by the SocketIOServer and
     * parses it for the students name, assignment type, document name, problem number, and number of errors.
     * These 'attributes' of the results event are formatted in an ArrayList with keys studentName, assignmentType,
     * documentName, problemNumber, and numErrors
     * Note: value associated with key numErrors will only ever be 0 or 1, as each results event is associated to one
     * error, not all of the errors from that expression event
     * @param data JSON String data from SocketIOServer
     * @throws JsonProcessingException Library Jackson is used to covert the JSON String to a HashMap. When there is an
     * error in processing the JSON String in ObjectMapper().readValue() method this exception is thrown.
     *
     */
    public void parseResult(String data) throws JsonProcessingException {

        //instantiate new HashMap from JSON String using jackson library
        Map<String, Object> tempResultsMap = new ObjectMapper().readValue(data, HashMap.class);

        //instantiate and add key-value pairs to a new HashMap
        HashMap<String, Object> resultsData = new HashMap<>();
        ArrayList<String> docInfo = getDocInfo((String)tempResultsMap.get("docname"));
        resultsData.put("studentName", docInfo.get(0));
        resultsData.put("assignmentType", docInfo.get(1));
        resultsData.put("documentName", docInfo.get(2));
        resultsData.put("problemNumber", tempResultsMap.get("problem"));
        resultsData.put("numErrors", checkForErrors((LinkedHashMap)tempResultsMap.get("value")));

        //temporarily prints the resultsData HashMap
        System.out.println(resultsData);
    }

    /**
     * Checks whether a results event contains an error
     * @param valueError holds whether this results event, anf thus a part of the problem, contains an error
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
     * Gets the student name, assignment type, and document title from the results event
     * @param docname name of the student document as taken from the results event
     * @return an ArrayList of Strings containing the students name, the assignment name, and the full document title
     */
    private ArrayList<String> getDocInfo(String docname){

        ArrayList<String> docInfo = new ArrayList<>();
        int periodIndex = docname.lastIndexOf(".");
        int slashIndex = docname.lastIndexOf("-");
        String studentName = docname.substring(0, slashIndex);
        String assignmentType = docname.substring(slashIndex + 1, periodIndex);
        String documentName = docname.substring(0, periodIndex);
        docInfo.add(studentName);
        docInfo.add(assignmentType);
        docInfo.add(documentName);
        return docInfo;

    }


}
