package Server;

import java.util.ArrayList;

public class ParseByDash implements DocumentNameParsingAlgorithm {
    /**
     * Parses for the student number and assignment type from a document with title of the form
     * StudentName-AssignmentType (spaces are allowed)
     * @param docname name of the student document as taken from the 'result' event
     * @return an ArrayList of Strings containing the students name, the assignment name, and the full document title
     */
    public ArrayList<String> getDocNameInfo(String docname) throws InvalidDocumentNameException{
        ArrayList<String> docInfo = new ArrayList<>();
        int periodIndex = docname.lastIndexOf(".");
        int slashIndex = docname.lastIndexOf("-");

        //if this document title does not contain a "-" throw an InvalidDocumentNameException
        if (slashIndex == -1){
            throw new InvalidDocumentNameException();
        }

        //replaceAll removes all of the white spaces in the string
        /*for example, if we had a document name Jasmina Brar -    Fractions1, the document name
        would become JasminaBrar-Fractions1*/
        String studentNum = docname.substring(0, slashIndex).replaceAll("\\s", "");
        String assignmentType = docname.substring(slashIndex + 1, periodIndex).replaceAll("\\s", "");
        String documentName = docname.substring(0, periodIndex).replaceAll("\\s", "");
        docInfo.add(studentNum);
        docInfo.add(assignmentType);
        docInfo.add(documentName);
        return docInfo;
    }
}
