package Test;
import Server.CheckMathParser;
import Server.InvalidDocumentNameException;
import Server.ParseByDash;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.*;
import java.util.ArrayList;
import java.util.HashMap;

public class parserTests {
    /**
     * Class for testing CheckMathParser, ParseByDash, ParsedDataPerAssignment, and ParsedDataPerAssignmentManager
     */
    private static HashMap<String, Object> parsedData;
    private ParseByDash docNameParser = new ParseByDash();


    @BeforeClass
    public static void setUp(){
        parsedData = new HashMap<String, Object>();
        parsedData.put("studentNum", "JasminaBrar");
        parsedData.put("assignmentType", "Fractions1");
        parsedData.put("documentName", "JasminaBrar-Fractions1");
        parsedData.put("problemNumber", 1);
        parsedData.put("numErrors", 3);
    }


    //ParseByDash Tests
    @Test
    public void getDocNameInfo_noSpaces(){
        String docName = "JasminaBrar-Fractions1.ezt*";
        try {
            ArrayList<String> docNameInfo = docNameParser.getDocNameInfo(docName);
            Assert.assertEquals(docNameInfo.get(0), "JasminaBrar");
            Assert.assertEquals(docNameInfo.get(1), "Fractions1");
            Assert.assertEquals(docNameInfo.get(2), "JasminaBrar-Fractions1");
        }catch(InvalidDocumentNameException e){
            System.out.println("Invalid Document Name");
        }

    }

    @Test
    public void getDocNameInfo_withSpaces(){
        String docName = "JasminaBrar    -    Fractions1.ezt*";
        try {
            ArrayList<String> docNameInfo = docNameParser.getDocNameInfo(docName);
            Assert.assertEquals(docNameInfo.get(0), "JasminaBrar");
            Assert.assertEquals(docNameInfo.get(1), "Fractions1");
            Assert.assertEquals(docNameInfo.get(2), "JasminaBrar-Fractions1");
        }catch(InvalidDocumentNameException e){
            System.out.println("Invalid Document Name");
        }
    }

    @Test
    public void getDocNameInfo_invalidDocName(){
        String docName = "JasminaBrar. Fractions1.ezt*";
        try {
            ArrayList<String> docNameInfo = docNameParser.getDocNameInfo(docName);
        }catch(InvalidDocumentNameException e){
            //to check that if an invalid document name is given that this error will be thrown
            System.out.println("Invalid Document Name");
            Assert.assertEquals(1, 1 );
        }
    }

    //CheckMathParser tests
    @Test
    public void parseFirstResultEvent(){
        /* Test that parseFirstResultEvent works correctly in CheckMathParser using a sample JSON String results event
         */

        String data = "{ \"docid\": \"864.6.12\", \"docname\": \"5-    Fractions1.ezt *\", \"userid\": 1288, \"username\": \"Jasmina Brar\", " +
                "\"mathid\": \"tex65.mth1288-8\", \"version\": 23, \"problem\": 2, \"value\": {\"type\": \"correct\"}}";
        CheckMathParser parser = new CheckMathParser();
        try {
            parser.parseFirstResultEvent(data);
            Assert.assertEquals(parser.getAssignmentName(), "5-Fractions1");
            Assert.assertEquals(parser.getAssignmentType(), "Fractions1");
            Assert.assertEquals(parser.getStudentNum(), "5");
            Assert.assertEquals((int)parser.getFinalParsedData().get("question2"), 0);
        }catch(JsonProcessingException e){
            System.out.println("Error" + e);
        }

    }

    @Test
    public void addParsedDataPreexistingQuestion(){
         /* Tests that addParsedData in CheckMathParser adds new data correctly if there already exists a question in
         finalParsedData (attribute of CheckMathParser) corresponding to the question new data corresponds to
          */
        String intialdata = "{ \"docid\": \"864.6.12\", \"docname\": \"5-    Fractions1.ezt *\", \"userid\": 1288, \"username\": \"Jasmina Brar\", " +
                "\"mathid\": \"tex65.mth1288-8\", \"version\": 23, \"problem\": 2, \"value\": {\"type\": \"correct\"}}";
        String newdata = "{ \"docid\": \"864.6.12\", \"docname\": \"5-    Fractions1.ezt *\", \"userid\": 1288, \"username\": \"Jasmina Brar\", " +
                "\"mathid\": \"tex65.mth1288-8\", \"version\": 23, \"problem\": 2, \"value\": {\"type\": \"math-error\"}}";
        CheckMathParser parser = new CheckMathParser();
        try {
            parser.parseFirstResultEvent(intialdata);
            parser.addParsedData(newdata);
            Assert.assertEquals((int)parser.getFinalParsedData().get("question2"), 1);
        }catch(JsonProcessingException e){
            System.out.println("Error" + e);
        }

    }

    @Test
    public void addParsedDataNewQuestion(){
        /*Tests that addParsedData in CheckMathParser adds new data correctly if there does not already exist a question
        in finalParsedData (attribute of CheckMathParser) corresponding to the question new data corresponds to
         */
        String intialdata = "{ \"docid\": \"864.6.12\", \"docname\": \"5-    Fractions1.ezt *\", \"userid\": 1288, \"username\": \"Jasmina Brar\", " +
                "\"mathid\": \"tex65.mth1288-8\", \"version\": 23, \"problem\": 2, \"value\": {\"type\": \"correct\"}}";
        String newdata = "{ \"docid\": \"864.6.12\", \"docname\": \"5-    Fractions1.ezt *\", \"userid\": 1288, \"username\": \"Jasmina Brar\", " +
                "\"mathid\": \"tex65.mth1288-8\", \"version\": 23, \"problem\": 3, \"value\": {\"type\": \"math-error\"}}";
        CheckMathParser parser = new CheckMathParser();
        try {
            parser.parseFirstResultEvent(intialdata);
            parser.addParsedData(newdata);
            Assert.assertEquals((int)parser.getFinalParsedData().get("question2"), 0);
            Assert.assertEquals((int)parser.getFinalParsedData().get("question3"), 1);
        }catch(JsonProcessingException e){
            System.out.println("Error" + e);
        }
    }


}
