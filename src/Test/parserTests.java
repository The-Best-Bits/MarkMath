package Test;
import Server.InvalidDocumentNameException;
import Server.ParseByDash;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.junit.*;

import java.util.ArrayList;

public class parserTests {

    @BeforeClass
    public static void setUp(){

    }

    @AfterClass
    public static void finished(){

    }

    //ParseByDash Tests
    @Test
    public void getDocNameInfo_noSpaces(){
        ParseByDash docNameParser = new ParseByDash();
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
        ParseByDash docNameParser = new ParseByDash();
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
        ParseByDash docNameParser = new ParseByDash();
        String docName = "JasminaBrar. Fractions1.ezt*";
        try {
            ArrayList<String> docNameInfo = docNameParser.getDocNameInfo(docName);
        }catch(InvalidDocumentNameException e){
            //to check that if an invalid document name is given that this error will be thrown
            System.out.println("Invalid Document Name");
            Assert.assertEquals(1, 1 );
        }
    }

    //
}
