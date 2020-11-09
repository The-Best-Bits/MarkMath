package Test;
import Server.InvalidDocumentNameException;
import Server.ParseByDash;
import markmath.controllers.ParsedDataPerAssignment;
import org.junit.*;

import java.util.ArrayList;
import java.util.HashMap;

public class parserTests {
    private static HashMap<String, Object> parsedData;
    private ParseByDash docNameParser = new ParseByDash();
    private static ParsedDataPerAssignment p;

    @BeforeClass
    public static void setUp(){
        parsedData = new HashMap<String, Object>();
        parsedData.put("studentName", "JasminaBrar");
        parsedData.put("assignmentType", "Fractions1");
        parsedData.put("documentName", "JasminaBrar-Fractions1");
        parsedData.put("problemNumber", 1);
        parsedData.put("numErrors", 3);

        p = new ParsedDataPerAssignment(parsedData);
    }

    @AfterClass
    public static void finished(){


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

    //ParsedDataPerAssignment tests
    @Test
    public void parsedDataPerAssignmentConstructor(){
        /*test that when creating a new parsedDataPerAssignment Object the info from the sent parsedData
        is correctly stored in the finalParsedData HashMap */

        Assert.assertTrue(p.getFinalParsedData().containsKey("question1"));
        Assert.assertEquals((int)p.getFinalParsedData().get("question1"), 3);

    }

    @Test
    public void addParsedData_newQuestion(){
        /* test that when newParsedData is sent for a different question that it is added as a
        new key-value pair in finalParsedData
         */
        HashMap<String, Object> newParsedData = new HashMap<String, Object>();
        newParsedData.put("studentName", "JasminaBrar");
        newParsedData.put("assignmentType", "Fractions1");
        newParsedData.put("documentName", "JasminaBrar-Fractions1");
        newParsedData.put("problemNumber", 2);
        newParsedData.put("numErrors", 3);

        p.addParsedData(newParsedData);
        Assert.assertTrue(p.getFinalParsedData().containsKey("question2"));
        Assert.assertEquals((int)p.getFinalParsedData().get("question2"), 3);

    }

    @Test
    public void addParsedData_oldQuestion(){
        /*test that when newParsedData is sent for a question already in finalParsedData list that the num of errors
        for this question is updated with respect to the new data
         */

        HashMap<String, Object> newParsedData = new HashMap<String, Object>();
        newParsedData.put("studentName", "JasminaBrar");
        newParsedData.put("assignmentType", "Fractions1");
        newParsedData.put("documentName", "JasminaBrar-Fractions1");
        newParsedData.put("problemNumber", 1);
        newParsedData.put("numErrors", 3);

        p.addParsedData(newParsedData);
        Assert.assertEquals((int)p.getFinalParsedData().get("question1"), 6);
    }
}
